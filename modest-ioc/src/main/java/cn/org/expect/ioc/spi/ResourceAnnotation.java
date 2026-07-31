package cn.org.expect.ioc.spi;

import java.lang.annotation.Annotation;
import javax.annotation.Resource;

import cn.org.expect.ioc.EasyResourceAnnotation;
import com.google.auto.service.AutoService;

/**
 * 描述 IoC 或脚本扩展点支持的注解契约
 */
@AutoService(EasyResourceAnnotation.class)
public class ResourceAnnotation implements EasyResourceAnnotation {

    public Class<? extends Annotation> getAnnotationClass() {
        return Resource.class;
    }

    public String getName(Annotation annotation) {
        return ((Resource) annotation).name();
    }
}
