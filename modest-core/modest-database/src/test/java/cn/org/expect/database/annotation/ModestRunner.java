package cn.org.expect.database.annotation;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.org.expect.Modest;
import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyBeanDefine;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.jdk.JavaDialect;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
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
    protected final static Log log = LogFactory.getLog(ModestRunner.class);

    /** 容器上下文信息 */
    public volatile EasyContext context;

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
        String line = StringUtils.left("", 100, '-');
        log.info(line + "\n> 单元测试类: " + this.getTestClass().getName() + "\n" + line);
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
                log.info(StringUtils.replaceLast(str.toString(), ", ", ""));
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
                    String name = StringUtils.trimBlank(annotation.value());
                    if (name.startsWith("$")) {
                        String part = name.substring(1);
                        if (part.length() > 2 && part.charAt(0) == '{' && part.charAt(part.length() - 1) == '}') {
                            String property = part.substring(1, part.length() - 1);
                            String value = this.getProperties().getProperty(property);
                            log.debug("向 " + test.getClass().getSimpleName() + " 注入属性: " + property + "=" + value);
                            javaDialect.setField(test, field, value);
                        }
                    }
                    continue;
                }

                EasyBeanDefine beanInfo = this.getContext().getBeanInfo(field.getType(), annotation.value());
                if (beanInfo != null) {
                    Object bean = beanInfo.getBean();
                    if (bean != null) {
                        log.debug("向 " + test.getClass().getSimpleName() + " 注入Bean: " + field.getName() + " = " + bean.getClass().getName());
                        javaDialect.setField(test, field, bean);
                        continue;
                    }
                }

                EasyBeanBuilder<?> beanBuilder = this.getContext().getBeanBuilder(field.getType());
                if (beanBuilder != null) {
                    Object bean = beanBuilder.getBean(this.getContext(), annotation.value(), this.getProperties(), test.getClass());
                    if (bean != null) {
                        log.debug("向 " + test.getClass().getSimpleName() + " 注入Bean: " + field.getName() + " = " + bean.getClass().getName());
                        javaDialect.setField(test, field, bean);
                        continue;
                    }
                }

                log.warn("向 " + test.getClass().getSimpleName() + " 注入Bean: " + field.getName() + " 失败！");
            }
        }

        return test;
    }

    protected EasyContext getContext() {
        if (this.context == null) {
            synchronized (this) {
                if (this.context == null) {
                    this.context = new DefaultEasyContext("sout+:info");
                }
            }
        }
        return this.context;
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
