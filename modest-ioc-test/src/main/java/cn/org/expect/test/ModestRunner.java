package cn.org.expect.test;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.test.annotation.RunWithLogSettings;
import cn.org.expect.test.annotation.RunWithProperties;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ModestRunner extends BlockJUnit4ClassRunner {

    /** Properties 文件分环境变量的参数名 */
    public final static String PROPERTY_ACTIVE_PROFILE = "active.test.env";

    /** 激活测试功能的参数名 */
    public final static String PROPERTY_ACTIVE_FEATURE = "active.test.feature";

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
        String testStr = type.getName() + "." + StringUtils.toString(method);
        Boolean value = this.cache.get(testStr);
        if (value != null) {
            return value;
        }

        // 设置日志参数
        RunWithLogSettings logSettings = type.getAnnotation(RunWithLogSettings.class);
        if (logSettings != null) {
            LogFactory.load(logSettings.value());
        }

        // 运行条件
        RunWithProperties annotation = type.getAnnotation(RunWithProperties.class);
        String[] values = annotation == null ? null : annotation.require();
        String filename = (annotation == null || StringUtils.isBlank(annotation.filename()) ? Settings.getProjectName() : annotation.filename()) + ".properties";
        String[] array = this.enableProperties();
        List<String> propertyNames = ArrayUtils.join(values, array);
        if (!propertyNames.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            Properties config = this.getProperties(type);
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
                        System.err.println(ResourcesUtils.getMessage("test.stderr.message003", filename, name, testStr));
                        this.cache.put(testStr, Boolean.TRUE);
                        return true;
                    }

                    try {
                        if (!NetUtils.ping(host)) {
                            System.err.println(ResourcesUtils.getMessage("test.stderr.message001", testStr, host));
                            this.cache.put(testStr, Boolean.TRUE);
                            return true;
                        }
                    } catch (Throwable e) {
                        System.err.println(ResourcesUtils.getMessage("test.stderr.message002", testStr, e));
                        this.cache.put(testStr, Boolean.TRUE);
                        return true;
                    }
                }
            }

            if (fail) {
                System.err.println(ResourcesUtils.getMessage("test.stderr.message003", filename, StringUtils.rtrimBlank(buf, ','), testStr));
                this.cache.put(testStr, Boolean.TRUE);
                return true;
            }
        }

        // 配置 JVM 参数
        RunWithFeature runWithFeature = type.getAnnotation(RunWithFeature.class);
        if (runWithFeature != null && runWithFeature.value() != null && runWithFeature.value().length > 0) {
            String[] activeFeatures = StringUtils.split(Settings.getVariable(PROPERTY_ACTIVE_FEATURE), ',');
            StringBuilder buf = new StringBuilder();
            boolean fail = false;
            for (String str : runWithFeature.value()) {
                String featureName = StringUtils.trimBlank(str);
                if (!StringUtils.inArrayIgnoreCase(featureName, activeFeatures)) {
                    buf.append(featureName).append(", ");
                    fail = true;
                }
            }

            if (fail) {
                System.err.println(ResourcesUtils.getMessage("test.stderr.message004", PROPERTY_ACTIVE_FEATURE, StringUtils.rtrimBlank(buf, ','), testStr));
                this.cache.put(testStr, Boolean.TRUE);
                return true;
            }
        }

        this.cache.put(testStr, Boolean.FALSE);
        return false;
    }

    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        super.runChild(method, notifier);
    }

    protected Object createTest() throws Exception {
        Object test = super.createTest();
        Properties properties = this.getProperties(test.getClass());
        this.getContext().addBean(properties);
        this.getContext().addProperties(properties);
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
    protected Properties getProperties(Class<?> type) {
        if (this.properties == null) {
            synchronized (this) {
                if (this.properties == null) {
                    RunWithProperties annotation = type.getAnnotation(RunWithProperties.class);
                    String filename = annotation == null || StringUtils.isBlank(annotation.filename()) ? Settings.getProjectName() : annotation.filename();
                    this.properties = FileUtils.loadProperties(this.getContext().getClassLoader(), filename + ".properties", PROPERTY_ACTIVE_PROFILE);
                }
            }
        }
        return this.properties;
    }
}
