package cn.org.expect.test.loader;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.bean.TestLoader;

@EasyBean
public class TestLoaderBuilder implements EasyBeanBuilder<TestLoader> {

    public TestLoader getBean(EasyContext context, Object... args) throws Exception {
        return null;
    }
}
