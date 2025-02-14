package cn.org.expect.ioc.spi;

import cn.org.expect.ioc.EasyBeanAnnotation;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.impl.DefaultBeanEntry;

public class BeanAnnotation implements EasyBeanAnnotation {

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
