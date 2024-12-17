package cn.org.expect.test.impl;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.bean.TestLoader;

@EasyBean(value = "1", description = "")
public class TestLoader2 implements TestLoader {
    @Override
    public void print() {
        System.out.println(TestLoader2.class.getName());
    }
}
