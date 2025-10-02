package cn.org.expect.ioc.impl.aware;

import cn.org.expect.ioc.EasyBeanAware;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.impl.Ceshi;
import com.google.auto.service.AutoService;

@AutoService(EasyBeanAware.class)
public class CeshiAwareOf implements EasyBeanAware {

    public Class<?> getInterfaceClass() {
        return CeshiAware.class;
    }

    public void execute(EasyContext ioc, Object object) {
        ((CeshiAware) object).setCeshi(ioc.getBean(Ceshi.class, "test1"));
    }
}
