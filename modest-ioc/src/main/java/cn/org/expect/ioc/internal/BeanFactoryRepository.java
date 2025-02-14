package cn.org.expect.ioc.internal;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyBeanInjector;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ClassUtils;

/**
 * 组件工厂集合
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/26
 */
public class BeanFactoryRepository {
    private final static Log log = LogFactory.getLog(BeanFactoryRepository.class);

    /** 泛型与组件工厂类映射关系 */
    private final HashMap<Class<?>, EasyBeanFactory<?>> map;

    public BeanFactoryRepository() {
        this.map = new HashMap<Class<?>, EasyBeanFactory<?>>();
    }

    /**
     * 注册组件
     *
     * @param type     类或接口
     * @param injector 组件工厂
     * @return 返回组件工厂对象
     */
    public EasyBeanFactory<?> create(Class<?> type, EasyBeanInjector injector) {
        if (ClassUtils.isAssignableFrom(EasyBeanFactory.class, type) && !Modifier.isAbstract(type.getModifiers())) {
            EasyBeanFactory<?> factory = injector.newInstance(type);
            if (this.add(factory, injector)) {
                return factory;
            }
        }
        return null;
    }

    /**
     * 添加组件工厂
     *
     * @param factory  组件工厂
     * @param injector 工具
     * @return 返回true表示成功，false表示失败
     */
    public boolean add(EasyBeanFactory<?> factory, EasyBeanInjector injector) {
        // 泛型信息
        String[] generics = ClassUtils.getInterfaceGenerics(factory.getClass(), EasyBeanFactory.class);
        Class<?> type = generics.length == 1 ? injector.forName(generics[0]) : null;
        if (type == null) {
            if (log.isWarnEnabled()) {
                log.warn("ioc.stdout.message016", factory.getClass().getName(), EasyBeanFactory.class.getName());
            }
            return false;
        }

        // 已存在
        if (this.map.containsKey(type)) {
            if (log.isWarnEnabled()) {
                log.warn("ioc.stdout.message017", factory.getClass().getName(), type.getName(), this.map.get(type).getClass().getName());
            }
            return false;
        }

        // 添加
        return this.add(type, factory);
    }

    /**
     * 注册组件工厂
     *
     * @param type    泛型信息
     * @param factory 组件工厂
     * @return 返回true表示成功，false表示失败
     */
    private boolean add(Class<?> type, EasyBeanFactory<?> factory) {
        EasyBeanFactory<?> value = this.map.get(type);
        if (value != null) {
            if (log.isWarnEnabled()) {
                log.warn("ioc.stdout.message035", type.getName(), factory.getClass().getName(), value.getClass().getName());
            }

            return false;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("ioc.stdout.message030", type.getName(), factory.getClass().getName());
            }

            this.map.put(type, factory);
            return true;
        }
    }

    /**
     * 返回组件工厂
     *
     * @param type 泛型信息
     * @return 组件工厂
     */
    public EasyBeanFactory<?> get(Class<?> type) {
        return this.map.get(type);
    }

    /**
     * 移除组件工厂
     *
     * @param type 泛型信息
     * @return 组件工厂
     */
    public EasyBeanFactory<?> remove(Class<?> type) {
        return this.map.remove(type);
    }

    /**
     * 返回组件工厂的泛型
     *
     * @return 泛型集合
     */
    public Set<Class<?>> getBeanClass() {
        return this.map.keySet();
    }

    /**
     * 清空组件工厂
     */
    public void clear() {
        this.map.clear();
    }
}
