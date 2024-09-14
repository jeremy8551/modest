package cn.org.expect.ioc.impl;

import cn.org.expect.annotation.EasyBean;

@EasyBean(name = "test2")
public class Ceshi2 implements Ceshi {
    
    public String getMessage() {
        return "test2";
    }
}
