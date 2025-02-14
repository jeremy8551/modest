package cn.org.expect.ioc;

/**
 * 组件实例接口
 */
public interface EasyBeanInstance {

    /**
     * 返回实例对象
     *
     * @param <E> 类信息
     * @return 单例对象
     */
    <E> E getBean();

    /**
     * 保存实例对象
     *
     * @param bean 实例对象
     */
    void setBean(Object bean);

    /**
     * 加锁
     */
    void lock();

    /**
     * 解锁
     */
    void unlock();
}
