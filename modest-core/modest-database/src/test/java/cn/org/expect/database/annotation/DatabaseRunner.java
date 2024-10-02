package cn.org.expect.database.annotation;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.org.expect.Modest;
import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.Jdbc;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.jdk.JavaDialect;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.ObjectUtils;
import cn.org.expect.util.StringUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class DatabaseRunner extends BlockJUnit4ClassRunner {

    public static String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    public static String JDBC_USER = "user";
    public static String JDBC_PASSWORD = "user";

    /** 容器上下文信息 */
    public volatile EasyContext context;

    /** 脚本引擎的环境变量集合 */
    protected volatile Properties properties;

    /** 缓存 */
    protected volatile Map<String, Boolean> cache;

    public DatabaseRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    protected DatabaseRunner(TestClass testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void run(RunNotifier notifier) {
        String line = StringUtils.left("", 100, '-');
        System.out.println(line + "\n> 单元测试类: " + this.getTestClass().getName() + "\n" + line);
        super.run(notifier);
    }

    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        return this.ignored(child) || super.isIgnored(child);
    }

    protected boolean ignored(FrameworkMethod child) {
        Class<?> cls = child.getDeclaringClass();
        if (this.cache == null) {
            this.cache = new Hashtable<String, Boolean>();
        }

        String key = cls.getName() + "." + child.getName();
        Boolean value = this.cache.get(key);
        if (value != null) {
            return value.booleanValue();
        }

        RunIf annotation = cls.getAnnotation(RunIf.class);
        String[] values = annotation == null ? null : annotation.values();
        String[] array = this.enableProperties();
        List<String> propertyNames = ArrayUtils.join(values, array);
        if (!propertyNames.isEmpty()) {
            StringBuilder str = new StringBuilder();
            str.append("不能运行测试案例 ");
            str.append(cls.getName());
            str.append(".");
            str.append(child.getName());
            str.append(", 因为未配置属性: ");

            boolean fail = false;
            for (String name : propertyNames) {
                if (!this.getProperties().contains(name)) {
                    str.append(name).append(", ");
                    fail = true;
                }
            }

            if (fail) {
                System.out.println(StringUtils.replaceLast(str.toString(), ", ", ""));
                this.cache.put(key, Boolean.TRUE);
                return true;
            }
        }

        this.cache.put(key, Boolean.FALSE);
        return false;
    }

    private Properties getProperties() {
        if (this.properties == null) {
            synchronized (this) {
                if (this.properties == null) {
                    this.properties = this.load();
                }
            }
        }
        return this.properties;
    }

    protected String[] enableProperties() {
        return new String[0];
    }

    @Override
    protected Object createTest() throws Exception {
        JavaDialect javaDialect = JavaDialectFactory.get();
        Object test = super.createTest();
        Field[] fields = test.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EasyBean.class)) {
                EasyBean annotation = field.getAnnotation(EasyBean.class);
                if (field.getType().getName().equals(String.class.getName())) {
                    String name = StringUtils.trimBlank(annotation.name());
                    if (name.startsWith("$")) {
                        String part = name.substring(1);
                        if (part.length() > 2 && part.charAt(0) == '{' && part.charAt(part.length() - 1) == '}') {
                            String property = part.substring(1, part.length() - 1);
                            String value = this.getProperties().getProperty(property);
                            System.out.println("向 " + test.getClass().getSimpleName() + " 注入属性: " + property + "=" + value);
                            javaDialect.setField(test, field, value);
                        }
                    }
                    continue;
                }

                if (field.getType().getName().equals(Connection.class.getName())) {
                    Connection conn = createConnection();
                    javaDialect.setField(test, field, conn);
                    continue;
                }

                if (field.getType().isAssignableFrom(EasyContext.class)) {
                    javaDialect.setField(test, field, this.getContext());
                    continue;
                }

//                EasyBeanDefine beanInfo = this.getContext().getBeanInfo(Connection.class, annotation.name());
//                if (beanInfo != null) {
//                    Object bean = beanInfo.getBean();
//                    javaDialect.setField(test, field, bean);
//                }

            }
        }

        return test;
    }

    protected EasyContext getContext() {
        if (this.context == null) {
            synchronized (this) {
                if (this.context == null) {
                    this.context = new DefaultEasyContext("sout:info");
                }
            }
        }
        return this.context;
    }

    protected Connection createConnection() {
        return Jdbc.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    protected Properties load() {
        try {
            Properties config = new Properties();
            config.load(ClassUtils.getResourceAsStream("/testconfig.properties"));

            String envmode = Modest.class.getPackage().getName() + ".test.mode";
            String mode = ObjectUtils.coalesce(System.getProperty(envmode), "home");
            InputStream in = ClassUtils.getResourceAsStream("/testconfig-" + mode + ".properties");
            if (in != null) {
                config.load(in);
                IO.closeQuietly(in);
            }
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
