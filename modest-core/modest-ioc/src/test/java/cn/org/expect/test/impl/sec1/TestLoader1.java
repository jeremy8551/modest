package cn.org.expect.test.impl.sec1;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.test.bean.TestLoader;

@EasyBean(value = "2", description = "")
public class TestLoader1 implements TestLoader {
    @Override
    public void print() {
        System.out.println(TestLoader1.class.getName());
    }
}
