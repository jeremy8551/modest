package cn.org.expect.ioc;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.impl.EasyetlBeanEventImpl;

/**
 * 组件的事件管理器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class EasyetlBeanEventManager {

    /** 容器上下文信息 */
    private EasyetlContext context;

    /** 事件监听器集合 */
    private List<EasyetlBeanEventListener> list;

    /**
     * 初始化
     *
     * @param context 容器上下文信息
     */
    public EasyetlBeanEventManager(EasyetlContext context) {
        this.list = new ArrayList<EasyetlBeanEventListener>();
        this.context = context;
    }

    /**
     * 添加事件监听器
     *
     * @param listener 监听器
     */
    public void addListener(EasyetlBeanEventListener listener) {
        this.list.add(listener);
    }

    /**
     * 添加组件信息
     *
     * @param beanInfo 组件信息
     */
    public void addBeanEvent(EasyetlBeanDefine beanInfo) {
        for (EasyetlBeanEventListener listener : this.list) {
            listener.addBean(new EasyetlBeanEventImpl(this.context, beanInfo));
        }
    }

    /**
     * 移除组件信息
     *
     * @param beanInfo 组件信息
     */
    public void removeBeanEvent(EasyetlBeanDefine beanInfo) {
        for (EasyetlBeanEventListener listener : this.list) {
            listener.removeBean(new EasyetlBeanEventImpl(this.context, beanInfo));
        }
    }

    /**
     * 移除所有监听器
     */
    public void clear() {
        this.list.clear();
    }

}
