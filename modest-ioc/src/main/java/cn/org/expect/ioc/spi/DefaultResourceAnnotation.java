package cn.org.expect.ioc.spi;

import java.lang.annotation.Annotation;

import cn.org.expect.ioc.EasyResourceAnnotation;
import cn.org.expect.ioc.annotation.EasyBean;
import com.google.auto.service.AutoService;

/**
 * 描述 IoC 或脚本扩展点支持的注解契约
 */
@AutoService(EasyResourceAnnotation.class)
public class DefaultResourceAnnotation implements EasyResourceAnnotation {

    public Class<? extends Annotation> getAnnotationClass() {
        return EasyBean.class;
    }

    public String getName(Annotation annotation) {
        return ((EasyBean) annotation).value();
    }
}
