package cn.org.expect.test.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.org.expect.log.LogSettings;

/**
 * 用于设置日志参数
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyLog {

    /**
     * 日志参数, 详见 {@linkplain LogSettings#load(String[])}
     *
     * @return 日志参数
     */
    String[] value();
}
