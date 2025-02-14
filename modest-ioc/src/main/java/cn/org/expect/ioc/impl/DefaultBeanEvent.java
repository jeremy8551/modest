package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanEvent;
import cn.org.expect.ioc.EasyContext;

/**
 * 接口实现类
 */
public class DefaultBeanEvent implements EasyBeanEvent {

    /** 实现类信息 */
    private final EasyBeanEntry entry;

    /** 容器上下文信息 */
    private final EasyContext context;

    /**
     * 组件变化事件
     *
     * @param context 容器上下文信息
     * @param entry   组件信息
     */
    public DefaultBeanEvent(EasyContext context, EasyBeanEntry entry) {
        this.context = context;
        this.entry = entry;
    }

    public EasyContext getContext() {
        return context;
    }

    public EasyBeanEntry getBeanEntry() {
        return entry;
    }
}
