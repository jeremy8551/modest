package cn.org.expect.test.loader;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyetlBeanBuilder;
import cn.org.expect.ioc.EasyetlContext;
import cn.org.expect.test.bean.TestLoader;

@EasyBean
public class TestLoaderBuilder implements EasyetlBeanBuilder<TestLoader> {

    public TestLoader getBean(EasyetlContext context, Object... args) throws Exception {
        return null;
    }
}
