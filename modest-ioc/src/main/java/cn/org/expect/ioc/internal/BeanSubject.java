package cn.org.expect.ioc.internal;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanListener;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.impl.DefaultBeanEvent;

/**
 * 负责维护观察者列表，并在自身状态改变时通知所有观察者。
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/26
 */
public class BeanSubject {

    /** 容器上下文信息 */
    private final EasyContext context;

    /** 事件监听器集合 */
    private final List<EasyBeanListener> list;

    /**
     * 初始化
     *
     * @param context 容器上下文信息
     */
    public BeanSubject(EasyContext context) {
        this.list = new ArrayList<EasyBeanListener>();
        this.context = context;
    }

    /**
     * 添加事件监听器
     *
     * @param listener 监听器
     */
    public void addListener(EasyBeanListener listener) {
        this.list.add(listener);
    }

    /**
     * 添加组件信息
     *
     * @param entry 组件信息
     */
    public void notifyAdd(EasyBeanEntry entry) {
        for (EasyBeanListener listener : this.list) {
            listener.addBean(new DefaultBeanEvent(this.context, entry));
        }
    }

    /**
     * 移除组件
     *
     * @param entry 组件信息
     */
    public void notifyRemove(EasyBeanEntry entry) {
        for (EasyBeanListener listener : this.list) {
            listener.removeBean(new DefaultBeanEvent(this.context, entry));
        }
    }

    /**
     * 移除所有监听器
     */
    public void clear() {
        this.list.clear();
    }
}
