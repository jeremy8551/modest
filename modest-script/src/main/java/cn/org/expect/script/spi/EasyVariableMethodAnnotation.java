package cn.org.expect.script.spi;

import cn.org.expect.ioc.EasyBeanAnnotation;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.impl.DefaultBeanEntry;
import cn.org.expect.script.annotation.EasyVariableMethod;
import com.google.auto.service.AutoService;

@AutoService(EasyBeanAnnotation.class)
public class EasyVariableMethodAnnotation implements EasyBeanAnnotation {

    public EasyVariableMethodAnnotation() {
    }

    public boolean isPresent(Class<?> type) {
        return type.isAnnotationPresent(EasyVariableMethod.class);
    }

    public EasyBeanEntry getBean(Class<?> type) {
        return new DefaultBeanEntry(type);
    }
}
