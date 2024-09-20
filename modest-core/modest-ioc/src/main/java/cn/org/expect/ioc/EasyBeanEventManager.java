package cn.org.expect.ioc;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.impl.EasyBeanEventImpl;

/**
 * 组件的事件管理器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class EasyBeanEventManager {

    /** 容器上下文信息 */
    private EasyContext context;

    /** 事件监听器集合 */
    private List<EasyBeanEventListener> list;

    /**
     * 初始化
     *
     * @param context 容器上下文信息
     */
    public EasyBeanEventManager(EasyContext context) {
        this.list = new ArrayList<EasyBeanEventListener>();
        this.context = context;
    }

    /**
     * 添加事件监听器
     *
     * @param listener 监听器
     */
    public void addListener(EasyBeanEventListener listener) {
        this.list.add(listener);
    }

    /**
     * 添加组件信息
     *
     * @param beanInfo 组件信息
     */
    public void addBeanEvent(EasyBeanDefine beanInfo) {
        for (EasyBeanEventListener listener : this.list) {
            listener.addBean(new EasyBeanEventImpl(this.context, beanInfo));
        }
    }

    /**
     * 移除组件信息
     *
     * @param beanInfo 组件信息
     */
    public void removeBeanEvent(EasyBeanDefine beanInfo) {
        for (EasyBeanEventListener listener : this.list) {
            listener.removeBean(new EasyBeanEventImpl(this.context, beanInfo));
        }
    }

    /**
     * 移除所有监听器
     */
    public void clear() {
        this.list.clear();
    }

}
