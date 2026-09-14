package cn.org.expect.ioc.spi;

import cn.org.expect.ioc.EasyBeanAware;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import com.google.auto.service.AutoService;

/**
 * 定义 IoC 容器感知对象的匹配规则
 */
@AutoService(EasyBeanAware.class)
public class EasyContextAwareOf implements EasyBeanAware {

    public Class<?> getInterfaceClass() {
        return EasyContextAware.class;
    }

    /** {@inheritDoc} */
    public void execute(EasyContext ioc, Object object) {
        ((EasyContextAware) object).setContext(ioc);
    }
}
