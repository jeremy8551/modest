package cn.org.expect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.org.expect.ModestRuntimeException;

/**
 * 类信息工具
 *
 * @author jeremy8551@gmail.com
 * @createtime 2011-05-24
 */
public class ClassUtils {

    /** 文件路径前缀 */
    public final static String PREFIX_CLASSPATH = "classpath:";

    /** 类加载器 */
    private static volatile ClassLoader CLASS_LOADER;

    /**
     * 设置默认类加载器
     *
     * @param classLoader 类加载器
     */
    public static void setClassLoader(ClassLoader classLoader) {
        ClassUtils.CLASS_LOADER = classLoader;
    }

    /**
     * 返回默认的类加载器
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        if (ClassUtils.CLASS_LOADER != null) {
            return ClassUtils.CLASS_LOADER;
        }

        // 当前线程的类加载器
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (Throwable e) {
            Logs.error(e.getLocalizedMessage(), e);
        }

        // 类信息的类加载器
        try {
            ClassLoader cl = ClassUtils.class.getClassLoader();
            if (cl != null) {
                return cl;
            }
        } catch (Throwable e) {
            Logs.error(e.getLocalizedMessage(), e);
        }

        // 系统类加载器
        try {
            return ClassLoader.getSystemClassLoader();
        } catch (Throwable e) {
            Logs.error(e.getLocalizedMessage(), e);
        }

        return null;
    }

    /**
     * 判断 type2 是否等于 type1，或 type2 是 type1的子类/接口
     *
     * @param type1 类信息
     * @param type2 类信息
     * @return 返回true表示 type2 等于 type1 或 type2 是 type1 的子类/接口
     */
    public static boolean isAssignableFrom(Class<?> type1, Class<?> type2) {
        Class<?> r1 = ClassUtils.getReference(type1);
        Class<?> r2 = ClassUtils.getReference(type2);
        return r1.isAssignableFrom(r2);
    }

    /**
     * 判断类信息参数 cls 是否在类信息集合参数 c 范围内（使用类信息全名是否相等来判断类信息是否相等）
     *
     * @param type 类信息
     * @param c    类信息集合
     * @return 返回 true 表示类信息在集合范围内
     */
    public static boolean inCollection(Class<?> type, Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }

        if (type == null) {
            for (Object o : c) {
                if (o == null) {
                    return true;
                }
            }
        } else {
            for (Object obj : c) {
                if (obj == null) {
                    continue;
                } else {
                    Class<?> cs = (Class<?>) (obj instanceof Class ? obj : obj.getClass());
                    if (cs.getName().equals(type.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断类信息参数 cls 是否在类信息数组参数 array 范围内（使用类信息全名是否相等来判断类信息是否相等）
     *
     * @param type  类信息
     * @param array 类信息数组
     * @return 返回 true 表示类信息在数组范围内，返回 false 表示类信息不再数组范围内
     */
    public static boolean inArray(Class<?> type, Class<?>... array) {
        if (type == null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                Class<?> cs = array[i];
                if (cs != null && cs.getName().equals(type.getName())) { // 判断类信息全名是否相等
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回对象或类参数 obj 类信息中的属性名
     *
     * @param obj  类信息
     * @param name 属性名
     * @return 格式：类全名.属性名
     * @throws RuntimeException 如果属性不存在则抛出异常
     */
    public static String toFieldName(Object obj, String name) {
        Field f = null;
        Class<?> cls = (obj instanceof Class) ? ((Class<?>) obj) : obj.getClass();
        try {
            f = cls.getDeclaredField(name);
        } catch (Throwable e) {
            Field[] array = cls.getDeclaredFields();
            for (Field field : array) {
                if (field.getName().equals(name)) {
                    f = field;
                    break;
                }
            }
        }

        if (f == null) {
            throw new RuntimeException(name);
        }
        return cls.getName() + "." + f.getName();
    }

    /**
     * 返回对象或类参数 obj 上的方法
     *
     * @param obj   类信息
     * @param name  方法名
     * @param types 方法上所有参数的类型
     * @return 格式：方法名(参数类1，参数类2，参数类3 ..)
     * @throws RuntimeException 如果方法不存在则抛出异常
     */
    public static String toMethodName(Object obj, String name, Class<?>... types) {
        Method method = ClassUtils.getMethod(obj, name, types);
        if (method == null) {
            throw new RuntimeException(name);
        }

        StringBuilder buf = new StringBuilder();
        buf.append(method.getName());
        buf.append("(");
        Class<?>[] array = method.getParameterTypes(); // 返回所有参数类型
        for (int i = 0; i < array.length; ) {
            String className = array[i].getSimpleName();
            buf.append(className);
            if (++i < array.length) {
                buf.append(", ");
            }
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * 调用java对象 obj 中的方法 name
     *
     * @param obj  JAVA对象
     * @param name 方法名
     * @param args 方法参数值
     * @return 方法返回值
     */
    public static Object executeMethod(Object obj, String name, Object... args) {
        Method method = ClassUtils.getMethod(obj, name);
        try {
            return method.invoke(obj, args);
        } catch (Throwable e) {
            throw new RuntimeException(obj.getClass().getSimpleName() + "." + name, e);
        }
    }

    /**
     * 在java对象中查找方法 name
     *
     * @param obj            JAVA对象
     * @param name           方法名
     * @param parameterTypes 方法的输入参数类型
     * @return 方法对象
     */
    public static Method getMethod(Object obj, String name, Class<?>... parameterTypes) {
        Class<?> cls = (obj instanceof Class) ? ((Class<?>) obj) : obj.getClass();
        try {
            return cls.getMethod(name, parameterTypes);
        } catch (Throwable e) {
            Method[] methods = cls.getDeclaredMethods();
            List<Method> list = new ArrayList<Method>(methods.length);
            for (Method method : methods) {
                if (method.getName().equals(name)) { // 判断方法名是否相等
                    list.add(method);
                }
            }

            if (list.isEmpty()) {
                return null;
            } else if (list.size() == 1) {
                return list.get(0);
            } else {
                for (Method method : list) {
                    Class<?>[] types = method.getParameterTypes();
                    if (types.length == parameterTypes.length) {
                        boolean b = true;
                        for (int i = 0; i < types.length; i++) {
                            if (!types[i].equals(parameterTypes[i])) { // 判断参数类型是否相等
                                b = false;
                                break;
                            }
                        }
                        if (b) {
                            return method;
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * 判断 java 类中是否有指定方法
     *
     * @param type           类信息
     * @param name           方法名
     * @param parameterTypes 方法参数
     * @return 返回true表示方法存在 false表示方法不存在
     */
    public static boolean containsMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        try {
            return type.getMethod(name, parameterTypes) != null;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (SecurityException e) {
            return false;
        }
    }

    /**
     * 读取并解析 java.class.path 类路径参数
     * <p>
     * /Users/etl/git/repository-atom/atom/target/classes
     * /Users/etl/git/repository-atom/atom/lib/db2java.jar
     * /Users/etl/git/repository-atom/atom/lib/db2jcc_license_cisuz.jar
     * /Users/etl/git/repository-atom/atom/lib/db2jcc_license_cu.jar
     * /Users/etl/git/repository-atom/atom/lib/db2jcc.jar
     * /Users/etl/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar
     *
     * @return 类路径数组（数组中没有空值）
     */
    public static String[] getClassPath() {
        String delimiter = System.getProperty("path.separator"); // 路径分隔符
        String classpath = System.getProperty("java.class.path");
        String[] array = StringUtils.removeBlank(StringUtils.split(classpath, delimiter));
        for (int i = 0; i < array.length; i++) {
            array[i] = StringUtils.decodeJvmUtf8HexString(array[i]);
        }
        return array;
    }

    /**
     * 返回 JAVA 类信息所在的 classpath 目录
     *
     * @param type 类信息
     * @return classpath目录绝对路径, 返回 null 表示未找到
     */
    public static File getClasspath(Class<?> type) {
        if (type == null) {
            throw new NullPointerException();
        }

        String[] array = ClassUtils.getClassPath();
        for (int i = 0; i < array.length; i++) {
            File dir = new File(array[i]);
            if (dir.exists() && dir.isDirectory()) {
                File parent = new File(dir, type.getPackage().getName().replace('.', '/'));
                if (parent.exists() && parent.isDirectory()) {
                    File[] files = parent.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            String name = file.getName();
                            if (FileUtils.getFilenameNoSuffix(name).equals(type.getSimpleName()) && FileUtils.getFilenameSuffix(name).equalsIgnoreCase("class")) {
                                return dir;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 查找资源
     *
     * @param name 资源名称, 例如: <br>
     *             /jdbc.properties <br>
     *             /images/show.gif <br>
     * @return 输入流
     */
    public static InputStream getResourceAsStream(String name) throws IOException {
        // 删除前缀 classpath:
        if (StringUtils.startsWith(name, ClassUtils.PREFIX_CLASSPATH, 0, true, true)) {
            int index = StringUtils.indexOf(name, ClassUtils.PREFIX_CLASSPATH, 0, true);
            name = StringUtils.trimBlank(name.substring(index + ClassUtils.PREFIX_CLASSPATH.length()));
        }

        // 文件路径
        if (FileUtils.isFile(name)) {
            return new FileInputStream(name);
        }

        // 删除左侧的 / 符号
        return ClassUtils.getClassLoader().getResourceAsStream(StringUtils.ltrim(name, '/'));
    }

    /**
     * 返回资源全名, 如：cn/org/expect/modest.xml
     *
     * @param type         资源文件所在Java包中的类, 如: cn.org.expect.Modest
     * @param resourceName 资源名, 如: modest.xml
     * @return 资源全名
     */
    public static String getResourceName(Class<?> type, String resourceName) {
        return type.getPackage().getName().replace('.', '/') + "/" + resourceName;
    }

    /**
     * 返回 class 信息的包名 <br>
     * getPackageName("cn.org.expect", 1) 返回字符串 cn <br>
     * getPackageName("cn.org.expect", 2) 返回字符串 cn.org <br>
     *
     * @param packageName 类名或包名
     * @param level       显示包名的级别
     *                    0表示显示原包名
     *                    -1表示从右向左边显示包名
     *                    1表示从左向右边显示包名
     * @return 包名
     */
    public static String getPackageName(String packageName, int level) {
        if (packageName == null || level == 0) {
            return packageName;
        }

        String[] array = StringUtils.split(packageName, '.');
        StringBuilder buf = new StringBuilder(packageName.length());

        if (level < 0) { // 从右向左边截取
            int i = array.length - (-level);
            if (i < 0) {
                i = 0;
            }

            for (; i >= 0 && i < array.length; ) {
                buf.append(array[i]);
                if (++i < array.length) {
                    buf.append('.');
                }
            }
        } else { // level > 0 从左向右边截取
            for (int i = 0, length = Math.min(level, array.length); i < length; ) {
                buf.append(array[i]);
                if (++i < length) {
                    buf.append('.');
                }
            }
        }

        return buf.toString();
    }

    /**
     * 返回 class 信息的包名 <br>
     * getPackageName("cn.org.expect", 1) 返回字符串 cn <br>
     * getPackageName("cn.org.expect", 2) 返回字符串 cn.org <br>
     *
     * @param type  类信息
     * @param level 显示包名的级别
     *              0表示显示原包名
     *              -1表示从右向左边显示包名
     *              1表示从左向右边显示包名
     * @return 包名
     */
    public static String getPackageName(Class<?> type, int level) {
        return getPackageName(type.getPackage().getName(), level);
    }

    /**
     * 判断字符串是数组
     *
     * @param generic （泛型转为）字符串
     * @return 返回true表示是，false表示否
     */
    public static boolean isGenericArray(String generic) {
        if (generic == null || generic.length() == 0) {
            return false;
        }

        if (generic.charAt(0) == '[') {
            if (generic.length() == 2) {
                return StringUtils.inArray(generic.charAt(1), 'B', 'S', 'I', 'J', 'F', 'D', 'C', 'Z');
            } else if (generic.length() > 2) {
                return generic.charAt(1) == 'L' && generic.charAt(generic.length() - 1) == ';' && generic.length() > 3;
            }
        }
        return false;
    }

    /**
     * 判断类信息是否是基础类型
     *
     * @param type 类信息
     * @return 返回true表示是，false表示否
     */
    public static boolean isPrimitive(Class<?> type) {
        return int.class.equals(type) //
            || long.class.equals(type) //
            || byte.class.equals(type) //
            || short.class.equals(type) //
            || char.class.equals(type) //
            || float.class.equals(type) //
            || double.class.equals(type) //
            || boolean.class.equals(type)  //
            ;
    }

    /**
     * 返回泛型数组的类信息
     *
     * @param generic 泛型数组的字符串
     * @return 类信息，基本类型返回 int, char ..
     */
    public static String getGenericArray(String generic) {
        if (generic != null && generic.length() >= 2 && generic.charAt(0) == '[') {
            int start = generic.charAt(1) == 'L' ? 2 : 1;
            int end = generic.charAt(generic.length() - 1) == ';' ? generic.length() - 1 : generic.length();
            String type = generic.substring(start, end);
            if (type.length() == 1) {
                char c = type.charAt(0);
                switch (c) {
                    case 'B':
                        return "byte";
                    case 'S':
                        return "short";
                    case 'I':
                        return "int";
                    case 'J':
                        return "long";
                    case 'F':
                        return "float";
                    case 'D':
                        return "double";
                    case 'C':
                        return "char";
                    case 'Z':
                        return "boolean";
                }
            }
            return type.length() == 0 ? null : type;
        }
        return null;
    }

    /**
     * 判断字符串参数className对应的Java类是否存在
     *
     * @param className java类全名
     * @return 类信息
     */
    @SuppressWarnings("unchecked")
    public static <E> Class<E> forName(String className) {
        try {
            return (Class<E>) Class.forName(className);
        } catch (ClassNotFoundException classNotFoundException) {
            if (Logs.isDebugEnabled()) {
                Logs.debug(ResourcesUtils.getMessage("class.stdout.message002", className));
            }
        } catch (Throwable e) {
            if (Logs.isDebugEnabled()) {
                Logs.debug(ResourcesUtils.getMessage("class.stdout.message001", className), e);
            }
        }
        return null;
    }

    /**
     * 判断字符串参数 className 对应的Java类是否存在
     * 不存在时会返回 null
     *
     * @param <E>        类信息
     * @param className  类名
     * @param initialize 是否初始化
     * @param loader     类加载器
     * @return 类信息
     */
    @SuppressWarnings("unchecked")
    public static <E> Class<E> forName(String className, boolean initialize, ClassLoader loader) {
        try {
            return (Class<E>) Class.forName(className, initialize, loader);
        } catch (ClassNotFoundException classNotFoundException) {
            if (Logs.isDebugEnabled()) {
                Logs.debug(ResourcesUtils.getMessage("class.stdout.message002", className));
            }
        } catch (Throwable e) {
            if (Logs.isDebugEnabled()) {
                Logs.debug(ResourcesUtils.getMessage("class.stdout.message001", className), e);
            }
        }
        return null;
    }

    /**
     * 判断字符串参数className对应的Java类是否存在
     *
     * @param className java类全名
     * @return 类信息
     */
    @SuppressWarnings("unchecked")
    public static <E> Class<E> loadClass(String className) {
        try {
            return (Class<E>) Class.forName(className);
        } catch (Throwable e) {
            throw new RuntimeException(className, e);
        }
    }

    /**
     * 判断字符串参数className对应的Java类是否存在
     *
     * @param className  类名
     * @param initialize 是否初始化
     * @param loader     类加载器
     * @return 类信息
     */
    @SuppressWarnings("unchecked")
    public static <E> Class<E> loadClass(String className, boolean initialize, ClassLoader loader) {
        try {
            return (Class<E>) Class.forName(className, initialize, loader);
        } catch (Throwable e) {
            throw new RuntimeException(className, e);
        }
    }

    /**
     * 生成一个类的实例对象
     *
     * @param type 类信息
     * @param <E>  类信息
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public static <E> E newInstance(Class<?> type) {
        if (type == null) {
            throw new NullPointerException();
        }

        try {
            return (E) type.newInstance();
        } catch (Throwable e) {
            throw new ModestRuntimeException("class.stdout.message003", type.getName(), e);
        }
    }

    /**
     * 生成一个类的实例对象
     *
     * @param classname   类名或Class对象
     * @param classLoader 类加载器
     * @param <E>         类信息
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public static <E> E newInstance(String classname, ClassLoader classLoader) {
        if (StringUtils.isBlank(classname)) {
            return null;
        }

        Class<?> cls = ClassUtils.forName(classname, true, classLoader);
        if (cls == null) {
            throw new ModestRuntimeException("class.stdout.message003", classname);
        }

        try {
            return (E) cls.newInstance();
        } catch (Throwable e) {
            throw new ModestRuntimeException("class.stdout.message003", cls.getName(), e);
        }
    }

    /**
     * 将对象中的 get 方法和 to 方法名和返回值转为字符串表格，用于调试打印对象内容
     *
     * @param obj          对象
     * @param deep         true 表示打印输出对象中的方法; false表示不打印输出对象的方法
     * @param ignoreCase   true表示忽略英文字母大小写
     * @param methodPrefix 方法名前缀数组
     * @return 字符图形表格
     */
    public static String toString(Object obj, boolean deep, boolean ignoreCase, String... methodPrefix) {
        if (obj == null) {
            return "";
        }

        String prefix = "obj.";
        CharTable table = new CharTable();
        table.addTitle("FUNCTION_NAME", CharTable.ALIGN_LEFT);
        table.addTitle("RETURN", CharTable.ALIGN_LEFT);

        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list, methodPrefix);

        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers())) { // 过滤抽象方法
                continue;
            }

            String functionName = method.getName();
            if (StringUtils.inArray(functionName, "getClass", "hashCode", "clone", "toString")) {
                continue;
            }

            Type[] types = method.getGenericParameterTypes();
            if (types == null || types.length == 0) {
                Class<?> cls = method.getReturnType();
                if (cls.equals(Void.class)) {
                    continue;
                }

                if (StringUtils.startsWith(functionName, list, ignoreCase)) {
                    try {
                        Object value = method.invoke(obj);
                        table.addCell(prefix + functionName);

                        if (value == null || value.getClass().getName().startsWith("java.") || !deep) {
                            table.addCell(value == null ? "" : StringUtils.toString(value));
                        } else {
                            table.addCell(toString(value, false, ignoreCase, methodPrefix));
                        }
                    } catch (Throwable e) {
                        table.addCell(prefix + functionName);
                        table.addCell(StringUtils.toString(e));
                    }
                }
            }
        }

        return table.toString(CharTable.Style.STANDARD);
    }

    /**
     * 将Class数组转为类名集合
     *
     * @param array Class类信息
     * @return 类名集合
     */
    public static List<String> asNameList(Class<?>[] array) {
        if (array == null) {
            return new ArrayList<String>(0);
        }

        List<String> list = new ArrayList<String>(array.length);
        for (Class<?> cls : array) {
            list.add(cls.getName());
        }
        return list;
    }

    /**
     * 将基础类型转为引用类型
     *
     * @param type 类信息
     * @return 引用类型
     */
    public static Class<?> getReference(Class<?> type) {
        if (int.class.equals(type)) {
            return Integer.class;
        }
        if (long.class.equals(type)) {
            return Long.class;
        }
        if (byte.class.equals(type)) {
            return Byte.class;
        }
        if (short.class.equals(type)) {
            return Short.class;
        }
        if (char.class.equals(type)) {
            return Character.class;
        }
        if (float.class.equals(type)) {
            return Float.class;
        }
        if (double.class.equals(type)) {
            return Double.class;
        }
        if (boolean.class.equals(type)) {
            return Boolean.class;
        }

        // 数组
        if (int[].class.equals(type)) {
            return Integer[].class;
        }
        if (long[].class.equals(type)) {
            return Long[].class;
        }
        if (byte[].class.equals(type)) {
            return Byte[].class;
        }
        if (short[].class.equals(type)) {
            return Short[].class;
        }
        if (char[].class.equals(type)) {
            return Character[].class;
        }
        if (float[].class.equals(type)) {
            return Float[].class;
        }
        if (double[].class.equals(type)) {
            return Double[].class;
        }
        if (boolean[].class.equals(type)) {
            return Boolean[].class;
        }

        // 引用类型
        return type;
    }

    /**
     * 返回类实现的所有接口，包括所有子类上的接口，以及接口继承的所有接口
     *
     * @param cls   类信息
     * @param array 接口的过滤器
     * @return 接口信息集合
     */
    public static List<Class<?>> getAllInterface(Class<?> cls, Filter... array) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        if (cls == null) {
            return list;
        }

        loadAllInterface(cls, array, list);

        // 查询父类上的接口
        Class<?> supcls = cls.getSuperclass();
        while (supcls != null) {
            loadAllInterface(supcls, array, list);
            supcls = supcls.getSuperclass();
        }
        return list;
    }

    /**
     * 查询类实现的所有接口，包括继承的所有接口
     *
     * @param type    类信息
     * @param filters 过滤器数组，通过过滤器后添加到集合 list
     * @param list    存储接口的集合
     */
    private static void loadAllInterface(Class<?> type, Filter[] filters, List<Class<?>> list) {
        Class<?>[] array = type.getInterfaces();
        if (array != null && array.length > 0) {
            for (Class<?> c : array) { // 判断是否有重复接口
                boolean add = true;
                for (Class<?> cl : list) {
                    if (ClassUtils.equals(cl, c)) { // 判断类名是否重复
                        add = false;
                        break;
                    }
                }

                if (add) {
                    if (filters != null && filters.length > 0) {
                        boolean accept = true;
                        for (Filter filter : filters) {
                            if (!filter.accept(c, c.getName())) {
                                accept = false;
                            }
                        }

                        if (accept) {
                            list.add(c);
                        }
                    } else {
                        list.add(c);
                    }
                }
            }

            for (Class<?> c : array) {
                loadAllInterface(c, filters, list);
            }
        }
    }

    /**
     * 返回类信息 {@code cls} 上指定接口 {@code interfacecls} 的泛型
     *
     * @param cls          类信息
     * @param interfacecls 类 {@code cls} 上实现的接口
     * @return 范型类型
     */
    public static String[] getInterfaceGenerics(Class<?> cls, Class<?> interfacecls) {
        Type[] types = cls.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                // pt.getTypeName().startsWith(interfacecls.getName())
                if (ptype.toString().startsWith(interfacecls.getName())) { // 判断接口名是否匹配
                    Type[] actualTypeArguments = ptype.getActualTypeArguments();
                    String[] array = new String[actualTypeArguments.length];
                    for (int i = 0; i < array.length; i++) {
                        // array[i] = actualTypeArguments[i].getTypeName();
                        String str = actualTypeArguments[i].toString();
                        String[] split = str.split("\\s+");
                        array[i] = split.length >= 2 ? split[1] : str;
                    }
                    return array;
                }
            }
        }
        return new String[0];
    }

    /**
     * 判断2个 Class 参数是否相等
     * <p>
     * 编写这个方法是因为 {@linkplain Class#equals(Object)} 方法不准确
     *
     * @param cls1 类信息
     * @param cls2 类信息
     * @return 返回true表示相等 返回false表示不等
     */
    public static boolean equals(Class<?> cls1, Class<?> cls2) {
        boolean b1 = cls1 == null;
        boolean b2 = cls2 == null;
        if (b1 && b2) {
            return true;
        } else if (b1 || b2) {
            return false;
        } else {
            return cls1.getName().equals(cls2.getName());
        }
    }

    public static boolean equals(Class<?>[] array1, Class<?>[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (!ClassUtils.equals(array1[i], array2[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断集合参数 {@code c}中是否包含参数 {@code cls}
     * <p>
     * 编写这个方法是因为 {@linkplain Class#equals(Object)} 方法不准确
     *
     * @param c   集合
     * @param cls 类信息
     * @return 返回true表示包含 false表示不包含
     */
    public static boolean contains(Collection<Class<?>> c, Class<?> cls) {
        if (c == null) {
            throw new NullPointerException();
        }

        for (Class<?> aClass : c) {
            if (ClassUtils.equals(cls, aClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 截取数组
     *
     * @param array 数组
     * @param begin 起始位置
     * @param end   结束位置（不包括）
     * @return 数组
     */
    public static Class<?>[] subarray(Class<?>[] array, int begin, int end) {
        if (array == null) {
            return null;
        }
        if (begin < 0) {
            throw new IllegalArgumentException(String.valueOf(begin));
        }

        int size = end - begin;
        if (size < 0 || end > array.length) {
            throw new IllegalArgumentException(String.valueOf(begin));
        }

        Class<?>[] newArray = new Class[size];
        System.arraycopy(array, begin, newArray, 0, newArray.length);
        return newArray;
    }

    /**
     * 返回异常是否与参数 cls 匹配，如果不匹配，则判断异常的Cause是否匹配
     *
     * @param e    异常
     * @param type 异常的类信息
     * @param <E>  类信息
     * @return 异常信息
     */
    @SuppressWarnings("unchecked")
    public static <E> E getCause(Throwable e, Class<E> type) {
        if (e == null || type == null) {
            throw new NullPointerException();
        }

        if (e.getClass().equals(type)) {
            return (E) e;
        } else {
            Throwable cause = e.getCause();
            return cause == null ? null : getCause(cause, type);
        }
    }

    /**
     * 在类路径classpath下查找包名最短的类（如果有多个，则只返回第一个）
     *
     * @param classLoader 类加载器
     * @param classpath   类路径
     * @return 包名集合
     */
    public static Set<String> findShortPackage(ClassLoader classLoader, String classpath) {
        if (StringUtils.isBlank(classpath)) {
            throw new IllegalArgumentException(classpath);
        }
        if (classLoader == null) {
            throw new NullPointerException();
        }

        File dir = new File(classpath);
        return ClassUtils.findShortPackage(classLoader, classpath, dir);
    }

    /**
     * 在类路径classpath下查找包名最短的类（如果有多个，则只返回第一个）
     *
     * @param classLoader 类加载器
     * @param classpath   类路径
     * @param dir         目录
     * @return 包名集合
     */
    private static Set<String> findShortPackage(ClassLoader classLoader, String classpath, File dir) {
        File[] files = FileUtils.array(dir.listFiles());

        Class<?> cls = findFirstClass(classLoader, classpath, files);
        if (cls != null) {
            Set<String> set = new HashSet<String>();
            set.add(cls.getPackage().getName());
            return set;
        }

        // 遍历目录
        Set<String> list = new HashSet<String>();
        for (File file : files) {
            if (file.isDirectory()) {
                list.addAll(ClassUtils.findShortPackage(classLoader, classpath, file));
            }
        }
        return list;
    }

    /**
     * 在文件数组中查找第一个类信息
     *
     * @param classLoader 类加载器
     * @param classpath   类路径
     * @param files       文件数组
     * @return 类信息
     */
    private static Class<?> findFirstClass(ClassLoader classLoader, String classpath, File[] files) {
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                if (file.getAbsolutePath().startsWith(classpath)) {
                    String lastfix = file.getAbsolutePath().substring(classpath.length());
                    String str = FileUtils.replaceFolderSeparator(StringUtils.ltrim(lastfix, '/', '\\'), '.');
                    String className = str.substring(0, str.length() - ".class".length());
                    Class<?> type = ClassUtils.forName(className, false, classLoader);
                    if (type != null) {
                        return type;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 在实例对象中搜索字段信息
     *
     * @param obj       实例对象
     * @param fieldName 字段名，大小写敏感
     * @return 字段信息
     */
    public static Field findField(Object obj, String fieldName) {
        if (obj == null) {
            return null;
        }

        Class<?> cls = obj.getClass();
        Field field;
        while ((field = ClassUtils.getField(cls, fieldName)) == null) {
            Class<?> superclass = cls.getSuperclass();
            if (superclass == null) {
                return null;
            } else {
                cls = superclass;
            }
        }
        return field;
    }

    public static Field getField(Class<?> cls, String fieldName) {
        if (cls == null) {
            throw new NullPointerException(fieldName);
        }

        try {
            return cls.getDeclaredField(fieldName);
        } catch (Throwable e) {
            return null;
        }
    }

    public interface Filter {

        /**
         * 过滤
         *
         * @param cls  接口的类信息
         * @param name 接口的全名
         * @return 返回true表示通过 false表示不通过
         */
        boolean accept(Class<?> cls, String name);
    }
}
