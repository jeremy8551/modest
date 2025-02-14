package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.annotation.EasyBean;

@EasyBean(value = "test2")
public class Ceshi2 implements Ceshi {

    public String getMessage() {
        return "test2";
    }
}
