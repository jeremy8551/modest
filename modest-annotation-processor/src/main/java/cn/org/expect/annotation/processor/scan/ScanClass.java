package cn.org.expect.annotation.processor.scan;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.org.expect.message.ResourceScanner;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Logs;
import cn.org.expect.util.StringUtils;

/**
 * 读取注解处理器生成的类索引
 */
public class ScanClass {

    /**
     * 加载指定父类型对应的实现类
     *
     * @param superType 父类或接口
     * @return 按索引顺序排列的实现类集合
     */
    public static Set<Class<?>> loadBySuper(Class<?> superType) {
        return loadBySuper(ClassUtils.getClassLoader(), superType);
    }

    /**
     * 加载指定父类型对应的实现类
     *
     * @param classLoader 类加载器
     * @param superType   父类或接口
     * @return 按索引顺序排列的实现类集合
     */
    public static Set<Class<?>> loadBySuper(ClassLoader classLoader, Class<?> superType) {
        String resourceName = ScanClassProcessor.SUPER_OUTPUT_PATH + superType.getName();
        return load(classLoader, resourceName);
    }

    /**
     * 加载指定注解对应的类
     *
     * @param annotationType 注解类型
     * @return 按索引顺序排列的类集合
     */
    public static Set<Class<?>> loadByAnnotation(Class<?> annotationType) {
        return loadByAnnotation(ClassUtils.getClassLoader(), annotationType);
    }

    /**
     * 加载指定注解对应的类
     *
     * @param classLoader    类加载器
     * @param annotationType 注解类型
     * @return 按索引顺序排列的类集合
     */
    public static Set<Class<?>> loadByAnnotation(ClassLoader classLoader, Class<?> annotationType) {
        String resourceName = ScanClassProcessor.ANNOTATION_OUTPUT_PATH + annotationType.getName();
        return load(classLoader, resourceName);
    }

    /**
     * 从类路径读取索引并加载其中的类
     *
     * @param classLoader  类加载器
     * @param resourceName 索引资源名称
     * @return 按索引顺序排列的类集合
     */
    private static LinkedHashSet<Class<?>> load(ClassLoader classLoader, String resourceName) {
        LinkedHashSet<Class<?>> classes = new LinkedHashSet<Class<?>>();
        ResourceScanner scanner = new ResourceScanner(classLoader, resourceName);
        while (scanner.hasNext()) {
            try {
                ArrayList<String> list = new ArrayList<String>();
                StringUtils.splitLines(new String(IO.read(scanner.next()), CharsetName.UTF_8), list);
                for (String className : list) {
                    if (StringUtils.isBlank(className)) {
                        continue;
                    }

                    try {
                        Class<?> type = ClassUtils.loadClass(StringUtils.trimBlank(className), true, classLoader);
                        classes.add(type);
                    } catch (Exception e) {
                        if (Logs.isDebugEnabled()) {
                            Logs.debug("load class {} fail!", className, e);
                        } else {
                            Logs.warn("load class {} fail!", className);
                        }
                    }
                }
            } catch (Exception e) {
                Logs.error(resourceName, e);
            }
        }
        return classes;
    }
}
