package cn.org.expect.ioc;

import java.util.HashMap;
import java.util.Set;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

/**
 * 组件工厂管理器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class EasyetlBeanBuilderManager {
    private final static Log log = LogFactory.getLog(EasyetlBeanBuilderManager.class);

    /** 容器上下文信息 */
    private EasyetlContext context;

    /** 组件接口与组件工厂类映射关系 */
    private HashMap<Class<?>, EasyetlBeanBuilder<?>> map;

    public EasyetlBeanBuilderManager(EasyetlContext context) {
        this.map = new HashMap<Class<?>, EasyetlBeanBuilder<?>>();
        this.context = Ensure.notNull(context);
    }

    /**
     * 注册组件
     *
     * @param type         组件的类信息
     * @param eventManager 事件管理器
     * @return 返回true表示注册成功，false表示注册失败
     */
    public boolean add(Class<?> type, EasyetlBeanEventManager eventManager) {
        // 如果没有实现 EasyBeanBuilder 接口
        if (!EasyetlBeanBuilder.class.isAssignableFrom(type)) {
            return false;
        }

        String[] generics = ClassUtils.getInterfaceGenerics(type, EasyetlBeanBuilder.class);
        if (generics.length == 1) {
            String className = generics[0]; // BeanBuilder 类的范型
            Class<Object> genCls = ClassUtils.forName(className, true, this.context.getClassLoader());
            if (genCls == null) {
                if (log.isDebugEnabled()) {
                    log.debug(ResourcesUtils.getMessage("class.standard.output.msg011", className));
                }
                return false;
            }

            EasyetlBeanBuilder<?> builder = this.context.createBean(type);
            if (this.add(genCls, builder)) {
                // 如果组件工厂实现了监听接口
                if (builder instanceof EasyetlBeanEventListener) {
                    eventManager.addListener((EasyetlBeanEventListener) builder);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 注册组件工厂
     *
     * @param type    组件的类信息
     * @param builder 组件工厂类
     * @return 返回true表示注册成功，false表示注册失败
     */
    public boolean add(Class<?> type, EasyetlBeanBuilder<?> builder) {
        EasyetlBeanBuilder<?> factory = this.map.get(type);
        if (factory != null) {
            if (log.isWarnEnabled()) {
                log.warn(ResourcesUtils.getMessage("class.standard.output.msg026", type.getName(), builder.getClass().getName(), factory.getClass().getName()));
            }
            return false;
        } else {
            if (log.isDebugEnabled()) {
                log.debug(ResourcesUtils.getMessage("class.standard.output.msg019", type.getName(), builder.getClass().getName()));
            }
            this.map.put(type, builder);
            return true;
        }
    }

    /**
     * 返回组件工厂
     *
     * @param type 组件的类信息
     * @return 组件工厂
     */
    public EasyetlBeanBuilder<?> get(Class<?> type) {
        return this.map.get(type);
    }

    /**
     * 移除组件工厂
     *
     * @param type 组件的类信息
     * @return 组件工厂
     */
    public EasyetlBeanBuilder<?> remove(Class<?> type) {
        return this.map.remove(type);
    }

    /**
     * 返回组件的类信息集合
     *
     * @return 组件的类信息集合
     */
    public Set<Class<?>> keySet() {
        return this.map.keySet();
    }

    /**
     * 清空所有组件工厂
     */
    public void clear() {
        this.map.clear();
    }

}
