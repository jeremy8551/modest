package cn.org.expect.ioc;

/**
 * 添加或删除组件的监听器
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-04-15
 */
public interface EasyBeanListener {

    /**
     * 添加组件实现类的监听接口
     *
     * @param event 事件
     */
    void addBean(EasyBeanEvent event);

    /**
     * 删除组件实现类的监听接口
     *
     * @param event 事件
     */
    void removeBean(EasyBeanEvent event);
}
