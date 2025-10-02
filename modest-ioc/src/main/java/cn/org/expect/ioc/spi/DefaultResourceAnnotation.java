package cn.org.expect.ioc.spi;

import java.lang.annotation.Annotation;

import cn.org.expect.ioc.EasyResourceAnnotation;
import cn.org.expect.ioc.annotation.EasyBean;
import com.google.auto.service.AutoService;

@AutoService(EasyResourceAnnotation.class)
public class DefaultResourceAnnotation implements EasyResourceAnnotation {

    public Class<? extends Annotation> getAnnotationClass() {
        return EasyBean.class;
    }

    public String getName(Annotation annotation) {
        return ((EasyBean) annotation).value();
    }
}
