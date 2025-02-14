package cn.org.expect.ioc.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.AutowireException;
import cn.org.expect.ioc.EasyBeanAware;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyResourceAnnotation;
import cn.org.expect.ioc.IocException;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.SPI;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 容器工厂接口实现类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/26
 */
public class BeanBuilder {
    private final static Log log = LogFactory.getLog(BeanBuilder.class);

    /** 容器上下文信息 */
    private final EasyContext context;

    /** 容器感知功能的集合 */
    private final List<EasyBeanAware> awareOfList;

    /** 自动注入注解的集合 */
    private final List<EasyResourceAnnotation> autowireList;

    public BeanBuilder(EasyContext context) {
        this.context = Ensure.notNull(context);
        this.awareOfList = SPI.load(context.getClassLoader(), EasyBeanAware.class);
        this.autowireList = SPI.load(context.getClassLoader(), EasyResourceAnnotation.class);
    }

    public <E> E newInstance(Class<?> type, Object... args) {
        if (Modifier.isAbstract(type.getModifiers())) { // 不能是接口或抽象类
            throw new IocException("ioc.stdout.message004", type.getName());
        }

        E instance = this.newInstance(type, new BeanArgument("", args)); // 创建对象
        this.autoAware(instance); // 注入特定资源
        this.autowire(instance); // 注入属性值
        return instance;
    }

    /**
     * 向实例对象注入<b>特定资源</b>
     *
     * @param object 对象
     */
    public void autoAware(Object object) {
        for (EasyBeanAware aware : this.awareOfList) {
            if (aware.getInterfaceClass().isAssignableFrom(object.getClass())) {
                aware.execute(this.context, object);
            }
        }
    }

    /**
     * 向实例对象注入<b>属性值</b>
     *
     * @param object 对象
     */
    public void autowire(Object object) {
        Class<?> type = object.getClass();

        // 本类中 private、public、protected 修饰的字段
        while (!Object.class.equals(type)) {
            for (Field field : type.getDeclaredFields()) {
                this.autowire(object, field);
            }
            type = type.getSuperclass();
        }
    }

    protected void autowire(Object object, Field field) {
        for (EasyResourceAnnotation autowiredAnnotation : this.autowireList) {
            Class<? extends Annotation> annotationClass = autowiredAnnotation.getAnnotationClass();
            if (annotationClass != null) {
                Annotation annotation = field.getAnnotation(annotationClass);
                if (annotation != null) {
                    this.autowire(object, field, annotation, StringUtils.trimBlank(autowiredAnnotation.getName(annotation)));
                }
            }
        }
    }

    /**
     * 向实例对象注入<b>属性值</b>
     *
     * @param object     对象
     * @param field      字段
     * @param annotation 字段上配置的注解
     * @param name       注解中的 name 属性
     */
    protected void autowire(Object object, Field field, Annotation annotation, String name) {
        Class<?> fieldClass = field.getType(); // 字段类型
        boolean primitive = ClassUtils.isPrimitive(fieldClass); // 基础数据类型有默认值
        if (!primitive && JavaDialectFactory.get().getField(object, field) != null) { // 如果属性值为null，则在容器中查找对应的对象
            return;
        }

        // 属性：name = "${propertyName}"
        if (name.startsWith("${") && name.endsWith("}")) {
            List<String> fieldNames = StringUtils.splitVariable(name, new ArrayList<String>());

            // 属性名: "${name}"
            if (fieldNames.size() == 1) {
                this.autowireProperty(object, field, fieldNames.get(0));
                return;
            }

            // 多个属性名: "${name},${value}"
            if (fieldNames.size() > 1) {
                throw new AutowireException("ioc.stdout.message010", object.getClass().getName(), field.getName(), annotation, StringUtils.toString(fieldNames));
            }

            // size == 0
            throw new AutowireException("ioc.stdout.message011", object.getClass().getName(), field.getName());
        }

        // 字符串、基础数据类型
        if (String.class.equals(fieldClass) || primitive) {
            // 属性值: name="Hello"，hello="10"
            if (StringUtils.isNotBlank(name)) {
                this.autowireProperty(object, field, name, name);
            } else {
                // 字段名作为属性名
                this.autowireProperty(object, field, field.getName());
            }
            return;
        }

        // 引用类型
        Object bean = this.context.getBeanQuietly(fieldClass, name, field.getName());
        if (bean != null) {
            JavaDialectFactory.get().setField(object, field, bean);
            return;
        }

        if (log.isWarnEnabled()) {
            log.warn("ioc.stdout.message007", object.getClass().getName(), field.getName(), name, fieldClass.getName());
        }
    }

    protected void autowireProperty(Object object, Field field, String propertyName) {
        String value = this.context.getProperty(propertyName);
        if (value == null) {
            if (log.isWarnEnabled()) {
                log.warn("ioc.stdout.message009", object.getClass().getName(), field.getName(), propertyName);
            }
            return;
        }

        this.autowireProperty(object, field, propertyName + "=" + value, value);
    }

    protected void autowireProperty(Object object, Field field, String propertyName, String propertyValue) {
        Object value; // 字段值
        Class<?> type = field.getType(); // 字段类型

        // 字符串
        if (String.class.equals(type)) {
            value = propertyValue;
        }

        // 基础数据类型
        else if ((value = StringUtils.parsePrimitive(type, propertyValue)) != null) {
        }

        // 其他类型
        else {
            Format format = this.context.getBean(Format.class);
            if (format != null) {
                try {
                    value = format.parseObject(propertyValue);
                } catch (Throwable e) {
                    throw new AutowireException("ioc.stdout.message012", object.getClass().getName(), field.getName(), propertyName, type.getName(), e);
                }
            }
        }

        if (value == null) {
            if (log.isWarnEnabled()) {
                log.warn("ioc.stdout.message013", object.getClass().getName(), field.getName(), propertyName, type.getName());
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("ioc.stdout.message008", object.getClass().getName(), field.getName(), value);
        }
        JavaDialectFactory.get().setField(object, field, value);
    }

    @SuppressWarnings("unchecked")
    protected <E> E newInstance(Class<?> type, BeanArgument argument) {
        BeanConstructor constructors = new BeanConstructor(type, argument);
        StringBuilder buf = new StringBuilder();

        // 优先使用参数匹配的构造方法
        if (constructors.getMatchConstructor() != null) {
            if (log.isDebugEnabled()) {
                log.debug("ioc.stdout.message001", type.getName(), constructors.getMatchConstructor().toGenericString());
            }

            Object[] args = argument.getArgs();
            try {
                return (E) constructors.getMatchConstructor().newInstance(args);
            } catch (Throwable e) {
                String message = ResourcesUtils.getMessage("ioc.stdout.message002", type.getName(), constructors.getMatchConstructor().toGenericString(), this.toString(args));
                buf.append(Settings.LINE_SEPARATOR).append(message);
                buf.append(StringUtils.toString(e));

                if (log.isDebugEnabled()) {
                    log.debug(message, e);
                }
            }
        }

        // 使用无参构造方法
        if (constructors.getBaseConstructor() != null) {
            if (log.isDebugEnabled()) {
                log.debug("ioc.stdout.message001", type.getName(), constructors.getBaseConstructor().toGenericString());
            }

            try {
                return (E) constructors.getBaseConstructor().newInstance();
            } catch (Throwable e) {
                String message = ResourcesUtils.getMessage("ioc.stdout.message002", type.getName(), constructors.getBaseConstructor().toGenericString(), "");
                buf.append(Settings.LINE_SEPARATOR).append(message);
                buf.append(StringUtils.toString(e));

                if (log.isDebugEnabled()) {
                    log.debug(message, e);
                }
            }
        }

        // 使用其他构造方法
        List<Constructor<?>> others = constructors.getConstructors();
        for (Constructor<?> constructor : others) {
            if (log.isDebugEnabled()) {
                log.debug("ioc.stdout.message001", type.getName(), constructor.toGenericString());
            }

            Object[] args = toParameters(constructor.getParameterTypes());
            if (args == null) {
                continue;
            }

            try {
                return (E) constructor.newInstance(args);
            } catch (Throwable e) {
                String message = ResourcesUtils.getMessage("ioc.stdout.message002", type.getName(), constructor.toGenericString(), this.toString(args));
                buf.append(Settings.LINE_SEPARATOR).append(message);
                buf.append(StringUtils.toString(e));

                if (log.isDebugEnabled()) {
                    log.debug(message, e);
                }
            }
        }

        throw new IocException("ioc.stdout.message003", type.getName(), buf);
    }

    // TODO 防止bean循环创建
    protected Object[] toParameters(Class<?>[] array) {
        Object[] args = new Object[array.length];
        for (int i = 0; i < args.length; i++) {
            Class<?> type = array[i];
            Object bean = this.context.getBean(type);
            if (bean != null) {
                args[i] = bean;
            } else {
                return null;
            }
        }
        return args;
    }

    /**
     * 将数组转为字符串
     *
     * @param args 数组
     * @return 字符串
     */
    protected StringBuilder toString(Object[] args) {
        StringBuilder buf = new StringBuilder();
        if (args.length > 0) {
            buf.append(Settings.LINE_SEPARATOR);
            buf.append(StringUtils.join(args, Settings.LINE_SEPARATOR));
            buf.append(Settings.LINE_SEPARATOR);
        }
        return buf;
    }
}
