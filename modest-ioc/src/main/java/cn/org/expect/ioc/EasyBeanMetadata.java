package cn.org.expect.ioc;

/**
 * 组件的元数据
 */
public interface EasyBeanMetadata {

    /**
     * 组件类
     *
     * @return 类信息
     */
    <E> Class<E> getType();

    /**
     * 组件名（忽略大小写） <br>
     * 组件类相同时，使用组件名区分
     *
     * @return 组件名
     */
    String getName();

    /**
     * 是否使用单例模式
     *
     * @return true表示使用单例模式, false表示使用原型模式（每次生成的组件都是新创建的）
     */
    boolean singleton();

    /**
     * 序号（序号大优先级高） <br>
     * 如果注册了多个同名的组件导致冲突，则使用序号最大的组件 <br>
     * 如果同名组件的（最大的）序号相等，则抛出异常
     *
     * @return 序号
     */
    int getOrder();

    /**
     * 组件是否延迟加载 <br>
     * 立即加载：容器启动时会初始化一个组件的实例对象，并注册到容器中 <br>
     * 延迟加载：容器启动时不会立即创建组件实例，当接收到请求时，才创建组件的实例对象 <br>
     *
     * @return 返回true表示延迟加载，返回false表示立即加载
     */
    boolean lazy();

    /**
     * 组件的说明信息
     *
     * @return 组件说明信息
     */
    String getDescription();
}
