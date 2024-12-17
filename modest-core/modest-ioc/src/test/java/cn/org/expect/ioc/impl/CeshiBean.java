package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;

public class CeshiBean {

    @EasyBean
    private EasyContext context;

    @EasyBean(value = "test2")
    private Ceshi ceshi;

    public CeshiBean() {
    }

    public EasyContext getContext() {
        return context;
    }

    public Ceshi getCeshi() {
        return ceshi;
    }
}
