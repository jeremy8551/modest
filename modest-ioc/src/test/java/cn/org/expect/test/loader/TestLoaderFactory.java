package cn.org.expect.test.loader;

import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.bean.TestLoader;

@EasyBean
public class TestLoaderFactory implements EasyBeanFactory<TestLoader> {

    public TestLoader build(EasyContext context, Object... args) throws Exception {
        return null;
    }
}
