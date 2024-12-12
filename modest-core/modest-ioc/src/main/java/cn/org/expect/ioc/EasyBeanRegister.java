package cn.org.expect.ioc;

/**
 * 组件的注册接口
 */
public interface EasyBeanRegister {

    /**
     * 注册组件
     *
     * @param beanClass 组件的类信息
     * @return 返回true表示注册成功 false表示注册失败（未添加组件）
     */
    boolean addBean(Class<?> beanClass);

    /**
     * 注册组件
     *
     * @param beanClass   组件的类信息
     * @param name        组件名
     * @param singleton   是否单例模式
     * @param lazy        是否延迟加载
     * @param priority    权重，如果注册了多个同名的组件导致冲突，容器使用权重值最大的组件
     * @param description 说明信息
     * @return 返回true表示注册成功 false表示注册失败（未添加组件）
     */
    boolean addBean(Class<?> beanClass, String name, boolean singleton, boolean lazy, int priority, String description);

    /**
     * 注册组件
     *
     * @param beanInfo 组件信息
     * @return 返回true表示注册成功 false表示注册失败（未添加组件）
     */
    boolean addBean(EasyBeanDefine beanInfo);

    /**
     * 注册单例模式组件
     *
     * @param bean 组件
     * @return 返回true表示注册成功 false表示注册失败（未添加组件）
     */
    boolean addSingletonBean(Object bean);
}
