package cn.org.expect.test.impl;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.bean.TestLoader;

@EasyBean(value = "1", description = "")
public class TestLoader2 implements TestLoader {
    private final static Log log = LogFactory.getLog(TestLoader2.class);

    public void print() {
        log.info(TestLoader2.class.getName());
    }
}
