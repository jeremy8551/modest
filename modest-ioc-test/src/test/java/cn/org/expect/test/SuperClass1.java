package cn.org.expect.test;

import cn.org.expect.ioc.annotation.EasyBean;

public class SuperClass1 {

    @EasyBean("${modest.log}")
    String log;

    @EasyBean("e")
    public char charVal;
}
