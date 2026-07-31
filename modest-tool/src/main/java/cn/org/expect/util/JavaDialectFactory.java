package cn.org.expect.util;

import cn.org.expect.ModestRuntimeException;

/**
 * JAVA 方言工厂
 *
 * @author jeremy8551@gmail.com
 */
public class JavaDialectFactory {

    /** 锁 */
    protected final static Object lock = new Object();

    /** JDK方言 */
    protected volatile static JavaDialect dialect;

    // 自动加载方言
    static {
        JavaDialectFactory.get();
    }

    private JavaDialectFactory() {
    }

    /**
     * 返回 JDK 方言对象
     *
     * @return 方言接口的实现类
     */
    public static JavaDialect get() {
        if (dialect == null) {
            synchronized (lock) {
                if (dialect == null) {
                    dialect = new JavaDialectFactory().build();
                }
            }
        }
        return dialect;
    }

    /**
     * 加载JDK方言接口的实现类
     *
     * @return JDK方言接口的实现类
     */
    public JavaDialect build() {
        int max = Settings.getJavaVersion(); // JDK 大版本号，如: 5, 6, 7, 8 ..
        String packageName = JavaDialectFactory.class.getPackage().getName(); // 方言类所在包名

        // 从 JDK5 开始
        Class<JavaDialect> type = null;
        for (int i = 5; i <= max; i++) {
            String className = packageName + ".Java" + i + "Dialect"; // 类全名
            Class<JavaDialect> dialectClass = ClassUtils.forName(className); // 加载方言类
            if (dialectClass != null) {
                type = dialectClass;
            }
        }

        if (type == null) {
            throw new ModestRuntimeException("No dialect for Java " + max);
        }

        return ClassUtils.newInstance(type);
    }
}
