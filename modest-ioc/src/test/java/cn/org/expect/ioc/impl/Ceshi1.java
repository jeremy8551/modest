package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.annotation.EasyBean;

@EasyBean(value = "test1")
public class Ceshi1 implements Ceshi {

    public String getMessage() {
        return "test1";
    }
}
