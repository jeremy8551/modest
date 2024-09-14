package cn.org.expect.ioc.impl;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyetlContext;

public class CeshiBean {

    @EasyBean
    private EasyetlContext context;

    @EasyBean(name = "test2")
    private Ceshi ceshi;

    public CeshiBean() {
    }

    public EasyetlContext getContext() {
        return context;
    }

    public Ceshi getCeshi() {
        return ceshi;
    }
}
