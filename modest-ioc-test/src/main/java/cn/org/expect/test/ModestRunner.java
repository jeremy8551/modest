package cn.org.expect.test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.org.expect.ModestRuntimeException;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.test.annotation.EasyRunIf;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ModestRunner extends BlockJUnit4ClassRunner {

    /** 参数名 */
    public final static String PROPERTY_ACTIVE_PROFILE = Settings.getPropertyName("active.profile");

    /** 容器上下文信息 */
    protected volatile EasyContext context;

    /** 脚本引擎的环境变量集合 */
    protected volatile Properties properties;

    /** 缓存 */
    protected volatile Map<String, Boolean> cache;

    public ModestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        LogFactory.getContext().reset();
    }

    public void run(RunNotifier notifier) {
        super.run(notifier);
    }

    protected boolean isIgnored(FrameworkMethod child) {
        return this.ignored(child) || super.isIgnored(child);
    }

    protected boolean ignored(FrameworkMethod child) {
        if (this.cache == null) {
            this.cache = new Hashtable<String, Boolean>();
        }

        Method method = child.getMethod();
        Class<?> type = child.getDeclaringClass();
        String key = type.getName() + "." + StringUtils.toString(method);
        Boolean value = this.cache.get(key);
        if (value != null) {
            return value;
        }

        // 设置日志参数
        EasyLog easyLog = type.getAnnotation(EasyLog.class);
        if (easyLog != null) {
            LogFactory.load(easyLog.value());
        }

        // 运行条件
        EasyRunIf annotation = type.getAnnotation(EasyRunIf.class);
        String[] values = annotation == null ? null : annotation.values();
        String[] array = this.enableProperties();
        List<String> propertyNames = ArrayUtils.join(values, array);
        if (!propertyNames.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            Properties config = this.getProperties();
            boolean fail = false;
            for (String name : propertyNames) {
                if (!config.containsKey(name)) {
                    buf.append(name).append(", ");
                    fail = true;
                }

                String[] split = StringUtils.split(name, '.');
                if ("host".equalsIgnoreCase(ArrayUtils.last(split))) {
                    String host = config.getProperty(name);
                    if (StringUtils.isBlank(host)) {
                        System.err.println(ResourcesUtils.getMessage("test.stderr.message003", key, name));
                        this.cache.put(key, Boolean.TRUE);
                        return true;
                    }

                    try {
                        if (!NetUtils.ping(host)) {
                            System.err.println(ResourcesUtils.getMessage("test.stderr.message001", key, host));
                            this.cache.put(key, Boolean.TRUE);
                            return true;
                        }
                    } catch (Throwable e) {
                        System.err.println(ResourcesUtils.getMessage("test.stderr.message002", key, e));
                        this.cache.put(key, Boolean.TRUE);
                        return true;
                    }
                }
            }

            if (fail) {
                System.err.println(ResourcesUtils.getMessage("test.stderr.message003", key, buf));
                this.cache.put(key, Boolean.TRUE);
                return true;
            }
        }

        this.cache.put(key, Boolean.FALSE);
        return false;
    }

    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        super.runChild(method, notifier);
    }

    protected Object createTest() throws Exception {
        Object test = super.createTest();
        Properties properties = this.getProperties();
        this.getContext().addBean(properties);
        this.getContext().add(properties, null);
        this.getContext().autowire(test);
        return test;
    }

    /**
     * 返回容器上下文信息
     *
     * @return 容器实例
     */
    protected EasyContext getContext() {
        if (this.context == null) {
            synchronized (this) {
                if (this.context == null) {
                    this.context = new DefaultEasyContext();
                }
            }
        }
        return this.context;
    }

    /**
     * 补充属性
     *
     * @return 属性数组
     */
    protected String[] enableProperties() {
        return new String[0];
    }

    /**
     * 返回配置属性
     *
     * @return 属性集合
     */
    protected Properties getProperties() {
        if (this.properties == null) {
            synchronized (this) {
                if (this.properties == null) {
                    this.properties = this.loadProperties();
                }
            }
        }
        return this.properties;
    }

    /**
     * 加载配置文件中的属性
     *
     * @return 属性集合
     */
    protected Properties loadProperties() {
        String yamlFileName = Settings.getProjectName();
        String fileName = yamlFileName + ".properties";
        try {
            ClassLoader classLoader = ClassUtils.getClassLoader(); // 类加载器

            // 加载配置文件
            Properties properties = new Properties();
            InputStream in = classLoader.getResourceAsStream(fileName);
            if (in != null) {
                properties.load(in);
            }

            // 加载分环境配置文件
            fileName = yamlFileName + "-" + StringUtils.coalesce(Settings.getProperty(PROPERTY_ACTIVE_PROFILE), "home") + ".properties";
            in = classLoader.getResourceAsStream(fileName);
            if (in != null) {
                properties.load(in);
                IO.closeQuietly(in);
            }

            return properties;
        } catch (Throwable e) {
            throw new ModestRuntimeException("test.stderr.message004", fileName, e);
        }
    }
}
