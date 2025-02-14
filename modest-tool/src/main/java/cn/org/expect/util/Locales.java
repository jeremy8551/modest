package cn.org.expect.util;

import java.util.Locale;

/**
 * Locale 帮助类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/2/19 14:58
 */
public class Locales {

    /** 语言地区信息 */
    public final static String PROPERTY_LOCALE = Settings.getPropertyName("locale");

    /**
     * 根据字符串参数 name 搜索国际化信息
     *
     * @param name 字符串，如：zh_CN
     * @return 返回 null 表示字符串参数 name错误
     */
    public static Locale lookup(String name) {
        try {
            Locale locale = new Locale(name);
            return StringUtils.isBlank(locale.toString()) ? null : locale;
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 返回 zh_CN 格式
     *
     * @return 格式
     */
    public static String toBaseName() {
        String localeStr = System.getProperty(Locales.PROPERTY_LOCALE);
        Locale locale = Locales.lookup(localeStr);
        if (locale == null) {
            locale = Locale.getDefault();
        }

        if (locale == null) {
            return "";
        }

        String language = locale.getLanguage();
        String country = locale.getCountry();
        StringBuilder buf = new StringBuilder();
        if (StringUtils.isNotBlank(language)) {
            buf.append('_').append(language);
        }
        if (StringUtils.isNotBlank(country)) {
            buf.append('_').append(country);
        }
        return buf.toString();
    }
}
