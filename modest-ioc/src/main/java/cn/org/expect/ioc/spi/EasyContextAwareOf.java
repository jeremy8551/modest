package cn.org.expect.ioc.spi;

import cn.org.expect.ioc.EasyBeanAware;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;

public class EasyContextAwareOf implements EasyBeanAware {

    public Class<?> getInterfaceClass() {
        return EasyContextAware.class;
    }

    public void execute(EasyContext ioc, Object object) {
        ((EasyContextAware) object).setContext(ioc);
    }
}
