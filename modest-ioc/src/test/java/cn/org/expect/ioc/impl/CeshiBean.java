package cn.org.expect.ioc.impl;

import javax.annotation.Resource;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.impl.aware.CeshiAware;

public class CeshiBean implements CeshiAware {

    @EasyBean
    private EasyContext context;

    @Resource(name = "test1")
    private Ceshi ceshi1;

    @EasyBean(value = "test2")
    private Ceshi ceshi2;

    private Ceshi aware;

    public CeshiBean() {
    }

    public EasyContext getContext() {
        return context;
    }

    public Ceshi getCeshi1() {
        return ceshi1;
    }

    public Ceshi getCeshi2() {
        return ceshi2;
    }

    public void setCeshi(Ceshi ceshi) {
        this.aware = ceshi;
    }

    public Ceshi getAware() {
        return aware;
    }
}
