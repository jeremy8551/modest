package cn.org.expect.ioc;

import java.lang.annotation.Annotation;

/**
 * 属性注入的注解，容器自动向配置了该注解的属性注入值 <br>
 * 通过实现 {@linkplain EasyResourceAnnotation} 接口，可以让 {@linkplain EasyContext} 容器识别 Spring 的 @autowired 注解
 */
public interface EasyResourceAnnotation {

    /**
     * 返回（属性注解的）注解类
     *
     * @return 类信息
     */
    Class<? extends Annotation> getAnnotationClass();

    /**
     * 返回注解中的属性名
     *
     * @param annotation 注解
     * @return 属性名
     */
    String getName(Annotation annotation);
}
