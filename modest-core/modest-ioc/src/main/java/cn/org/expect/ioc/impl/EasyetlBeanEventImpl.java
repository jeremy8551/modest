package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.EasyetlBeanEvent;
import cn.org.expect.ioc.EasyetlBeanDefine;
import cn.org.expect.ioc.EasyetlContext;

/**
 * 接口实现类
 */
public class EasyetlBeanEventImpl implements EasyetlBeanEvent {

    /** 实现类信息 */
    private EasyetlBeanDefine beanInfo;

    /** 容器上下文信息 */
    private EasyetlContext context;

    /**
     * 组件变化事件
     *
     * @param context  容器上下文信息
     * @param beanInfo 组件信息
     */
    public EasyetlBeanEventImpl(EasyetlContext context, EasyetlBeanDefine beanInfo) {
        this.context = context;
        this.beanInfo = beanInfo;
    }

    public EasyetlContext getContext() {
        return context;
    }

    public EasyetlBeanDefine getBeanInfo() {
        return beanInfo;
    }

}
