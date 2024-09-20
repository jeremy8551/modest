package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.EasyBeanEvent;
import cn.org.expect.ioc.EasyBeanDefine;
import cn.org.expect.ioc.EasyContext;

/**
 * 接口实现类
 */
public class EasyBeanEventImpl implements EasyBeanEvent {

    /** 实现类信息 */
    private EasyBeanDefine beanInfo;

    /** 容器上下文信息 */
    private EasyContext context;

    /**
     * 组件变化事件
     *
     * @param context  容器上下文信息
     * @param beanInfo 组件信息
     */
    public EasyBeanEventImpl(EasyContext context, EasyBeanDefine beanInfo) {
        this.context = context;
        this.beanInfo = beanInfo;
    }

    public EasyContext getContext() {
        return context;
    }

    public EasyBeanDefine getBeanInfo() {
        return beanInfo;
    }

}
