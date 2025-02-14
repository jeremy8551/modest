package cn.org.expect.test;

import cn.org.expect.ioc.annotation.EasyBean;

public class SuperClass2 extends SuperClass1 {

    @EasyBean("true")
    protected boolean booleanVal;

    @EasyBean("1100")
    long longVal;
}
