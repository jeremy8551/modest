package cn.org.expect.ioc.spi;

import java.lang.annotation.Annotation;
import javax.annotation.Resource;

import cn.org.expect.ioc.EasyResourceAnnotation;

public class ResourceAnnotation implements EasyResourceAnnotation {

    public Class<? extends Annotation> getAnnotationClass() {
        return Resource.class;
    }

    public String getName(Annotation annotation) {
        return ((Resource) annotation).name();
    }
}
