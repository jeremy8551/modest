package cn.org.expect.script.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脚本命令编译器的注解: 使用注解可以自定义脚本命令
 *
 * @author jeremy8551@gmail.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyCommandCompiler {

    /**
     * 脚本命令前缀
     *
     * @return 命令前缀，如: echo
     */
    String[] name();

    /**
     * 关键字 <br>
     * 被定义为关键字后不能在变量名中使用
     *
     * @return 关键字集合
     */
    String[] keywords() default {};
}
