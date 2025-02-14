package cn.org.expect.script.spi;

import cn.org.expect.ioc.EasyBeanAnnotation;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.impl.DefaultBeanEntry;
import cn.org.expect.script.annotation.EasyVariableExtension;

public class EasyVariableExtensionAnnotation implements EasyBeanAnnotation {

    public EasyVariableExtensionAnnotation() {
    }

    public boolean isPresent(Class<?> type) {
        return type.isAnnotationPresent(EasyVariableExtension.class);
    }

    public EasyBeanEntry getBean(Class<?> type) {
        return new DefaultBeanEntry(type);
    }
}
