package cn.org.expect.test.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.org.expect.test.ModestRunner;

/**
 * 测试哪些功能
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RunWithFeature {

    /**
     * 测试哪些功能, 与 {@linkplain ModestRunner} 类配合使用
     *
     * @return 属性数组
     */
    String[] value();
}
