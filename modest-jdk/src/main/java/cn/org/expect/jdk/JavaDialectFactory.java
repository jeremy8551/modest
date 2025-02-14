package cn.org.expect.jdk;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Logs;
import cn.org.expect.util.Settings;

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

    /** 方言类集合(按JDK版本号从小到大排序) */
    public final static List<Class<JavaDialect>> DIALECT_CLASS_LIST = new ArrayList<Class<JavaDialect>>();

    /** 方言类对应的 JDK 版本号 */
    public final static List<Integer> DIALECT_CLASS_VERSION_LIST = new ArrayList<Integer>();

    // 自动加在方言
    static {
        JavaDialectFactory.get();
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

    /** 类加载器 */
    private final JavaDialectClassLoader classLoader = new JavaDialectClassLoader();

    /**
     * 加载JDK方言接口的实现类
     *
     * @return JDK方言接口的实现类
     */
    public JavaDialect build() {
        String packageName = JavaDialectFactory.class.getPackage().getName(); // 方言类所在包名
        int max = Settings.getJDKVersion(); // JDK 大版本号，如: 5, 6, 7, 8 ..

        // 从 JDK5 开始
        for (int i = 5; i <= max; i++) {
            String name = "JDK" + i;
            InputStream in = JavaDialect.class.getResourceAsStream(name);
            if (in == null) {
                continue;
            }

            // 类全名
            String className = packageName + ".JDK" + i;
            if (Logs.isDebugEnabled()) {
                Logs.debug("jdk.stdout.message001", "/" + JavaDialectClassLoader.class.getPackage().getName().replace('.', '/') + "/" + name, className);
            }

            // 方言类
            Class<JavaDialect> type = null;
            try {
                type = this.classLoader.loadClass(className, in);
            } catch (Throwable e) {
                if (Logs.isErrorEnabled()) {
                    Logs.error(name + ", " + className, e);
                }
            }

            if (type != null) {
                JavaDialectFactory.DIALECT_CLASS_LIST.add(type);
                JavaDialectFactory.DIALECT_CLASS_VERSION_LIST.add(i);
            }
        }

        if (JavaDialectFactory.DIALECT_CLASS_LIST.isEmpty()) {
            throw new UnsupportedOperationException();
        } else {
            int last = JavaDialectFactory.DIALECT_CLASS_LIST.size() - 1;
            return ClassUtils.newInstance(JavaDialectFactory.DIALECT_CLASS_LIST.get(last));
        }
    }
}
