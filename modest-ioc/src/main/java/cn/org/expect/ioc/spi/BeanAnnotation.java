package cn.org.expect.ioc.spi;

import cn.org.expect.ioc.EasyBeanAnnotation;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.impl.DefaultBeanEntry;
import com.google.auto.service.AutoService;

/**
 * 描述 IoC 或脚本扩展点支持的注解契约
 */
@AutoService(EasyBeanAnnotation.class)
public class BeanAnnotation implements EasyBeanAnnotation {

    /** {@inheritDoc} */
    public boolean isPresent(Class<?> type) {
        return type.isAnnotationPresent(EasyBean.class);
    }

    public EasyBeanEntry getBean(Class<?> type) {
        EasyBean annotation = type.getAnnotation(EasyBean.class); // 取得类上配置的注解
        DefaultBeanEntry entry = new DefaultBeanEntry(type);
        entry.setName(annotation.value());
        entry.setSingleton(annotation.singleton());
        entry.setOrder(annotation.order());
        entry.setLazy(annotation.lazy());
        entry.setDescription(annotation.description());
        entry.setBean(null);
        return entry;
    }
}
