package cn.org.expect.script.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 变量方法注解：可以自定义变量方法
 *
 * @author jeremy8551@gmail.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyVariableMethod {

    /**
     * 变量的类信息（哪些类的实例可以执行变量方法）
     *
     * @return 变量的类信息
     */
    Class<?> variable();

    /**
     * 变量方法名（一个方法可以多个方法名）
     *
     * @return 变量方法名数组
     */
    String name();

    /**
     * 参数类型
     *
     * @return 参数个数
     */
    Class<?>[] parameters() default {};

    /**
     * 参数注释
     *
     * @return 参数注释数组
     */
    String[] parameterNote() default {};

    /**
     * 判断 {@linkplain #parameters()} 最后右侧的类信息是一个（Varargs Method）可变参数（String... array）
     *
     * @return 返回true表示是，false表示否
     */
    boolean varargs() default false;

    /**
     * 变量方法是否是单例模式（每次使用同一个对象执行变量方法）<br>
     * 如果不支持单例模式，则每次会使用一个新的变量方法执行任务
     *
     * @return 返回true表示单例模式
     */
    boolean singleton() default true;
}
