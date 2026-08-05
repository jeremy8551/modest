package cn.org.expect.annotation.processor.scan;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.org.expect.message.ResourceScanner;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Logs;
import cn.org.expect.util.StringUtils;

/**
 * 读取 Dao 方法参数索引，并按反射方法查询源码中的参数名
 */
public class ScanMethod {

    /** Dao 类与方法参数信息 */
    private final Map<Class<?>, Map<Method, List<String>>> map;

    /**
     * 使用当前线程上下文类加载器读取索引
     */
    public ScanMethod() {
        this(ClassUtils.getClassLoader());
    }

    /**
     * 使用指定类加载器读取索引
     *
     * @param classLoader 类加载器，不能为 null
     */
    public ScanMethod(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new NullPointerException();
        }

        this.map = new HashMap<Class<?>, Map<Method, List<String>>>();
        this.load(classLoader);
    }

    /**
     * 查询反射方法对应的有序参数名
     *
     * @param method 反射方法，不能为 null
     * @return 方法参数信息，索引中不存在时返回 null
     */
    public List<String> getParameterNames(Method method) {
        if (method == null) {
            throw new NullPointerException();
        }

        Map<Method, List<String>> methods = this.map.get(method.getDeclaringClass());
        return methods == null ? null : methods.get(method);
    }

    /**
     * 读取类路径中的所有 Dao 方法参数索引
     *
     * @param classLoader 类加载器
     */
    protected void load(ClassLoader classLoader) {
        String resourceName = ScanMethodProcessor.DAO_METHOD_PARAMETER_FILE;
        ResourceScanner scanner = new ResourceScanner(classLoader, resourceName);
        while (scanner.hasNext()) {
            try {
                List<String> list = scanner.readFileLines(CharsetName.UTF_8);
                for (String line : list) {
                    if (StringUtils.isNotBlank(line)) {
                        this.parseLine(classLoader, line);
                    }
                }
            } catch (Throwable e) {
                Logs.error("读取文件失败: " + resourceName, e);
            }
        }
    }

    /**
     * 解析一行方法参数索引
     *
     * @param classLoader 类加载器
     * @param line        索引行
     */
    protected void parseLine(ClassLoader classLoader, String line) {
        String[] fields = StringUtils.trimBlank(StringUtils.split(line, ','));
        if (fields.length < 2) {
            throw new IllegalArgumentException(line);
        }

        try {
            Class<?> daoClass = Class.forName(fields[0], false, classLoader);
            String name = fields[1];
            List<String> parameterNames = new ArrayList<String>();

            Class<?>[] array = new Class<?>[fields.length - 2];
            for (int i = 2; i < fields.length; i++) {
                String[] property = StringUtils.splitProperty(fields[i]);
                if (property == null) {
                    throw new IllegalArgumentException(line);
                }

                array[i - 2] = Class.forName(property[0], false, classLoader);
                parameterNames.add(property[1]);
            }

            Method method = daoClass.getMethod(name, array);
            Ensure.notNull(method);
            this.put(daoClass, method, parameterNames);
        } catch (Exception e) {
            Logs.error("解析方法参数索引失败: " + line, e);
        }
    }

    /**
     * 保存 Dao 方法参数信息
     *
     * @param daoClass  Dao类
     * @param method    方法键
     * @param parameter 参数信息
     */
    protected void put(Class<?> daoClass, Method method, List<String> parameter) {
        Map<Method, List<String>> map = this.map.get(daoClass);
        if (map == null) {
            map = new LinkedHashMap<Method, List<String>>();
            this.map.put(daoClass, map);
        }
        map.put(method, parameter);
    }
}
