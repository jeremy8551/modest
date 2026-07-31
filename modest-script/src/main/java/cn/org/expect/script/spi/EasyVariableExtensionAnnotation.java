package cn.org.expect.script.spi;

import cn.org.expect.ioc.EasyBeanAnnotation;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.impl.DefaultBeanEntry;
import cn.org.expect.script.annotation.EasyVariableExtension;
import com.google.auto.service.AutoService;

/**
 * 描述 IoC 或脚本扩展点支持的注解契约
 */
@AutoService(EasyBeanAnnotation.class)
public class EasyVariableExtensionAnnotation implements EasyBeanAnnotation {

    /**
     * 初始化 EasyVariableExtensionAnnotation
     */
    public EasyVariableExtensionAnnotation() {
    }

    /** {@inheritDoc} */
    public boolean isPresent(Class<?> type) {
        return type.isAnnotationPresent(EasyVariableExtension.class);
    }

    public EasyBeanEntry getBean(Class<?> type) {
        return new DefaultBeanEntry(type);
    }
}
