package cn.org.expect.test.impl;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.test.bean.TestLoader;

@EasyBean(name = "1", description = "")
public class TestLoader2 implements TestLoader {
    @Override
    public void print() {
        System.out.println(TestLoader2.class.getName());
    }
}
