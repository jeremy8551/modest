package cn.org.expect.ioc.impl;

import cn.org.expect.annotation.EasyBean;

@EasyBean(value = "test1")
public class Ceshi1 implements Ceshi {
    public String getMessage() {
        return "test1";
    }
}
