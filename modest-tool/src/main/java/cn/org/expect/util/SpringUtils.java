package cn.org.expect.util;

import java.lang.reflect.AnnotatedElement;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Spring工具类
 */
public final class SpringUtils {

    private SpringUtils() {
    }

    /**
     * 判断方法是否为请求处理方法
     *
     * @param method 方法
     * @return true表示请求处理方法
     */
    public static boolean isMappingAnnotationPresent(AnnotatedElement method) {
        return method.isAnnotationPresent(RequestMapping.class) //
            || method.isAnnotationPresent(GetMapping.class) //
            || method.isAnnotationPresent(PostMapping.class) //
            || method.isAnnotationPresent(PutMapping.class) //
            || method.isAnnotationPresent(DeleteMapping.class) //
            || method.isAnnotationPresent(PatchMapping.class) //
            || method.isAnnotationPresent(Mapping.class) //
            ;
    }
}
