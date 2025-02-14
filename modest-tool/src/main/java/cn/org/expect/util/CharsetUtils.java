package cn.org.expect.util;

import java.nio.charset.Charset;

/**
 * 字符集帮助类
 */
public class CharsetUtils {

    /** 默认字符集 */
    public final static String PROPERTY_CHARSET = Settings.getPropertyName("charset");

    /** 默认字符集 */
    public static String charset;

    static {
        String charset = System.getProperty(CharsetUtils.PROPERTY_CHARSET);
        if (charset == null || charset.length() == 0) {
            CharsetUtils.charset = System.getProperty("file.encoding");
        } else {
            CharsetUtils.charset = charset;
        }
    }

    /**
     * 返回默认字符集
     *
     * @return 字符集
     */
    public static String get() {
        return charset;
    }

    /**
     * 返回字符集
     *
     * @param charsetName 字符集
     * @return 字符集
     */
    public static String get(String charsetName) {
        return StringUtils.isBlank(charsetName) ? CharsetUtils.get() : charsetName;
    }

    /**
     * 根据字符串参数 charsetName 搜索字符集信息
     *
     * @param name 字符集名(如: UTF-8)
     * @return 返回 null 表示字符串参数 name错误
     */
    public static Charset lookup(String name) {
        try {
            return Charset.forName(name);
        } catch (Throwable x) {
            return null;
        }
    }
}
