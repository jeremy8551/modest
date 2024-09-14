package cn.org.expect.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脚本引擎变量方法的工厂类配置注解信息
 *
 * @author jeremy8551@qq.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScriptFunction {

    /**
     * 变量方法名
     *
     * @return 变量方法名
     */
    String name();

    /**
     * 变量方法关键字
     *
     * @return 变量方法关键字
     */
    String[] keywords() default {};

}
