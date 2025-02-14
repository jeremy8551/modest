package cn.org.expect.ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件注解 <br>
 * IOC容器启动时会扫描带 {@linkplain EasyBean} 注解的类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-08
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyBean {

    /**
     * 组件名
     *
     * @return 组件名
     */
    String value() default "";

    /**
     * 是否使用单例模式
     *
     * @return true表示使用单例模式, false表示使用原型模式（每次生成的组件都是新创建的）
     */
    boolean singleton() default false;

    /**
     * 是否使用延迟加载模式
     *
     * @return true表示单例组件使用延迟加载模式
     */
    boolean lazy() default true;

    /**
     * 序号 <br>
     * 如果注册了多个同名的组件导致冲突，容器使用序号值最大的组件
     *
     * @return 默认0
     */
    int order() default 0;

    /**
     * 描述信息
     *
     * @return 描述信息
     */
    String description() default "";
}
