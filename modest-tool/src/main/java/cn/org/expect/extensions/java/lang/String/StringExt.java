package cn.org.expect.extensions.java.lang.String;

import cn.org.expect.util.StringUtils;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

/**
 * java.lang.String 的扩展方法
 */
@Extension
public final class StringExt {

    public static String[] split(@This String str, char separator) {
        return StringUtils.split(str, separator);
    }

    public static boolean isBlank(@This String str) {
        return StringUtils.isBlank(str);
    }
}