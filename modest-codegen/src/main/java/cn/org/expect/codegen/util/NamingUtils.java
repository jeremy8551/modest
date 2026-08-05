package cn.org.expect.codegen.util;

import java.util.Locale;

/**
 * 数据库名称与 Java 名称转换工具
 */
public final class NamingUtils {

    private NamingUtils() {
    }

    /**
     * 转换为大驼峰名称
     *
     * @param value 原始名称
     * @return 大驼峰名称
     */
    public static String upperCamel(String value) {
        StringBuilder result = new StringBuilder();
        String[] parts = value.split("[^A-Za-z0-9]+");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.length() > 0) {
                result.append(Character.toUpperCase(part.charAt(0)));
                result.append(part.substring(1).toLowerCase(Locale.ENGLISH));
            }
        }
        return result.toString();
    }

    /**
     * 转换为小驼峰名称
     *
     * @param value 原始名称
     * @return 小驼峰名称
     */
    public static String lowerCamel(String value) {
        String upperCamel = upperCamel(value);
        return upperCamel.length() == 0 ? upperCamel : Character.toLowerCase(upperCamel.charAt(0)) + upperCamel.substring(1);
    }

    /**
     * 转换为合法枚举常量
     *
     * @param value 数据字典键
     * @return 枚举常量
     */
    public static String enumConstant(String value) {
        String constant = value.trim().replaceAll("[^A-Za-z0-9]+", "_").toUpperCase(Locale.ENGLISH);
        return constant.length() == 0 || Character.isDigit(constant.charAt(0)) ? "VALUE_" + constant : constant;
    }
}
