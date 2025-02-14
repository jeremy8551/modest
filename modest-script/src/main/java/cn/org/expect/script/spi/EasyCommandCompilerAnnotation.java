package cn.org.expect.script.spi;

import java.lang.reflect.Modifier;

import cn.org.expect.ioc.EasyBeanAnnotation;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.impl.DefaultBeanEntry;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.ClassUtils;

public class EasyCommandCompilerAnnotation implements EasyBeanAnnotation {

    public boolean isPresent(Class<?> type) {
        return type.isAnnotationPresent(EasyCommandCompiler.class) //
            && ClassUtils.isAssignableFrom(UniversalCommandCompiler.class, type) //
            && !Modifier.isAbstract(type.getModifiers()) //
            ;
    }

    public EasyBeanEntry getBean(Class<?> type) {
        return new DefaultBeanEntry(type);
    }
}
