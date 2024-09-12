package cn.org.expect.jdk;

import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Settings;

/**
 * JAVA 方言工厂
 *
 * @author jeremy8551@qq.com
 */
public class JavaDialectFactory {

    /** JDK方言 */
    private volatile static JavaDialect dialect;

    /**
     * 返回 JDK 方言对象
     *
     * @return 方言接口的实现类
     */
    public static JavaDialect get() {
        if (dialect == null) {
            synchronized (JavaDialectFactory.class) {
                if (dialect == null) {
                    dialect = new JavaDialectFactory().loadDialect();
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
    private JavaDialect loadDialect() {
        String packageName = JavaDialect.class.getPackage().getName(); // 方言类所在包名
        int major = Settings.getJDKVersion(); // JDK大版本号，如: 5, 6, 7, 8 ..

        // 查找JDK版本号对应的方言类
        Class<JavaDialect> cls = null;
        while (major >= 0 && (cls = ClassUtils.forName(packageName + ".JDK" + major)) == null) {
            major--;
        }
        return ClassUtils.newInstance(cls);
    }

}
