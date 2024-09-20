package cn.org.expect.ioc.impl;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;

public class CeshiBean {

    @EasyBean
    private EasyContext context;

    @EasyBean(name = "test2")
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
