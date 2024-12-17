package cn.org.expect.test;

import java.util.Properties;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.ArrayUtils;

@EasyBean
public class PropertiesBuilder implements EasyBeanBuilder<Properties> {

    public Properties getBean(EasyContext context, Object... args) throws Exception {
        return ArrayUtils.indexOf(args, Properties.class, 0);
    }
}
