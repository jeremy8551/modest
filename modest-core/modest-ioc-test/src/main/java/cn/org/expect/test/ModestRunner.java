package cn.org.expect.test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.org.expect.Modest;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyBeanDefine;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.jdk.JavaDialect;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogSettings;
import cn.org.expect.test.annotation.RunIf;
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

public class ModestRunner extends BlockJUnit4ClassRunner {
    protected static Log log;

    /** 参数名 */
    public static String YAML_FILE_NAME = "modest";

    /** 参数名 */
    public final static String ACTIVE_PROFILE = Modest.class.getPackage().getName() + ".test.mode";

    /** 容器上下文信息 */
    protected volatile EasyContext context;

    /** 脚本引擎的环境变量集合 */
    protected volatile Properties properties;

    /** 缓存 */
    protected volatile Map<String, Boolean> cache;

    public ModestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    protected ModestRunner(TestClass testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void run(RunNotifier notifier) {
        LogSettings.load(LogFactory.getContext(), "sout+");
        log = LogFactory.getLog(ModestRunner.class);

        String line = StringUtils.left("", 100, '-');
        log.info(line + "\n> 单元测试类: " + this.getTestClass().getName() + "\n" + line);
        super.run(notifier);
    }

    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        return this.ignored(child) || super.isIgnored(child);
    }

    protected boolean ignored(FrameworkMethod child) {
        if (this.cache == null) {
            this.cache = new Hashtable<String, Boolean>();
        }

        Class<?> cls = child.getDeclaringClass();
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
                if (!this.getProperties().containsKey(name)) {
                    str.append(name).append(", ");
                    fail = true;
                }
            }

            if (fail) {
                log.info(StringUtils.replaceLast(str.toString(), ", ", ""));
                this.cache.put(key, Boolean.TRUE);
                return true;
            }
        }

        this.cache.put(key, Boolean.FALSE);
        return false;
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        log.info("> 执行测试方法: {}", method.getName());
        super.runChild(method, notifier);
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        Field[] fields = test.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EasyBean.class)) { // 属性上配置了注解
                this.autowired(test, field);
            }
        }
        return test;
    }

    /**
     * 向测试类的实例对象中注入Bean
     *
     * @param test  测试类的实例对象
     * @param field 注入bean的属性
     * @throws Exception 发生错误
     */
    protected void autowired(Object test, Field field) throws Exception {
        JavaDialect javaDialect = JavaDialectFactory.get();
        EasyBean annotation = field.getAnnotation(EasyBean.class);

        // 注入配置文件中的属性
        List<String> fieldNames = StringUtils.splitVariable(annotation.value(), new ArrayList<String>());
        for (String name : fieldNames) {
            Class<?> type = field.getType();
            String value = this.getProperties().getProperty(name);
            log.info("向 " + test.getClass().getSimpleName() + " 注入属性(" + type.getSimpleName() + ") " + name + "=" + value);

            if ("int".equalsIgnoreCase(type.getSimpleName())) {
                javaDialect.setField(test, field, Integer.parseInt(value));
            } else {
                javaDialect.setField(test, field, value);
            }
        }
        if (!fieldNames.isEmpty()) {
            return;
        }

        // 注入Bean
        EasyContext context = this.getContext();
        EasyBeanDefine beanInfo = context.getBeanInfo(field.getType(), annotation.value());
        if (beanInfo != null) {
            Object bean = beanInfo.getBean();
            if (bean != null) {
                log.debug("向 " + test.getClass().getSimpleName() + " 注入Bean: " + field.getName() + " = " + bean.getClass().getName());
                javaDialect.setField(test, field, bean);
                return;
            }
        }

        // 使用Bean工厂创建对象，并注入
        EasyBeanBuilder<?> beanBuilder = context.getBeanBuilder(field.getType());
        if (beanBuilder != null) {
            Object bean = beanBuilder.getBean(context, annotation.value(), this.getProperties(), test.getClass());
            if (bean != null) {
                log.debug("向 " + test.getClass().getSimpleName() + " 注入Bean: " + field.getName() + " = " + bean.getClass().getName());
                javaDialect.setField(test, field, bean);
                return;
            }
        }

        log.warn("向 " + test.getClass().getSimpleName() + " 注入Bean: " + field.getName() + " 失败！");
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
                    this.context = new DefaultEasyContext("sout+");
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
        try {
            Properties config = new Properties();
            config.load(ClassUtils.getResourceAsStream("/" + YAML_FILE_NAME + ".properties"));
            String active = ObjectUtils.coalesce(System.getProperty(ACTIVE_PROFILE), "home");
            InputStream in = ClassUtils.getResourceAsStream("/" + YAML_FILE_NAME + "-" + active + ".properties");
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
