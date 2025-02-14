package cn.org.expect.test.impl.sec1;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.bean.TestLoader;

@EasyBean(value = "2", description = "")
public class TestLoader1 implements TestLoader {
    private final static Log log = LogFactory.getLog(TestLoader1.class);

    public void print() {
        log.info(TestLoader1.class.getName());
    }
}
