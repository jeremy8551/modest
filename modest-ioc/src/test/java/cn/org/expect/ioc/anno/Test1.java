package cn.org.expect.ioc.anno;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.org.expect.ioc.annotation.EasyBean;
import org.junit.Assert;
import org.junit.Test;

@EasyBean("test1")
public class Test1 {

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException {
        Annotation annotation = Test1.class.getAnnotation(EasyBean.class);
        Method[] methods = annotation.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase("value")) {
                Assert.assertEquals("test1", method.invoke(annotation));
            }
        }
    }
}
