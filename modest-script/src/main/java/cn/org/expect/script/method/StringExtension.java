package cn.org.expect.script.method;

import cn.org.expect.script.annotation.EasyVariableExtension;
import cn.org.expect.util.StringUtils;

@EasyVariableExtension
public class StringExtension {

    /**
     * 获取字符串中某个位置上的字符
     *
     * @param str   字符串
     * @param index 位置信息，从0开始
     * @return 字符
     */
    public static String charAt(CharSequence str, int index) {
        return String.valueOf(str.charAt(index));
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static String charAt(CharSequence str, long index) {
        return String.valueOf(str.charAt((int) index));
    }

    /**
     * 将字符串转为布尔值
     *
     * @param str 字符串
     * @return 布尔值
     */
    public static Boolean booleanValue(CharSequence str) {
        return Boolean.parseBoolean(str.toString());
    }

    /**
     * 在字符串中搜索
     *
     * @param str  字符串
     * @param dest 搜索的内容
     * @return 位置信息，从0开始，-1表示未搜索到
     */
    public static int indexOf(CharSequence str, CharSequence dest) {
        return StringUtils.indexOf(str, dest, 0, false);
    }

    /**
     * 在字符串中搜索
     *
     * @param str  字符串
     * @param dest 搜索的内容
     * @param from 搜索起始位置，从 0 开始
     * @return 位置信息，从0开始，-1表示未搜索到
     */
    public static int indexOf(CharSequence str, CharSequence dest, int from) {
        return StringUtils.indexOf(str, dest, from, false);
    }

    /**
     * 在字符串中搜索
     *
     * @param str        字符串
     * @param dest       搜索的内容
     * @param from       搜索起始位置，从 0 开始
     * @param ignoreCase true表示忽略大小写
     * @return 位置信息，从0开始，-1表示未搜索到
     */
    public static int indexOf(CharSequence str, CharSequence dest, int from, boolean ignoreCase) {
        return StringUtils.indexOf(str, dest, from, ignoreCase);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static int indexOf(CharSequence str, CharSequence dest, long from) {
        return StringUtils.indexOf(str, dest, (int) from, false);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static int indexOf(CharSequence str, CharSequence dest, long from, boolean ignoreCase) {
        return StringUtils.indexOf(str, dest, (int) from, ignoreCase);
    }

    /**
     * 判断字符串是否是空白
     *
     * @param str 字符串
     * @return 返回true表示空白，false表示不是空白
     */
    public static boolean isBlank(CharSequence str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 返回字符串长度
     *
     * @param str 字符串
     * @return 长度
     */
    public static int length(CharSequence str) {
        return str.length();
    }

    /**
     * 将字符串转为小写
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String lower(CharSequence str) {
        return str.toString().toLowerCase();
    }

    /**
     * 将字符串转为大写
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String upper(CharSequence str) {
        return str.toString().toUpperCase();
    }

    /**
     * 删除字符串二端的空白字符
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String trim(CharSequence str) {
        return StringUtils.trimBlank(str);
    }

    /**
     * 删除字符串二端的空白字符与字符参数
     *
     * @param str   字符串
     * @param chars 字符参数
     * @return 字符串
     */
    public static String trim(CharSequence str, String chars) {
        return StringUtils.trimBlank(str, chars.toCharArray());
    }

    /**
     * 删除字符串左侧的空白字符
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String ltrim(CharSequence str) {
        return StringUtils.ltrimBlank(str);
    }

    /**
     * 删除字符串左侧的空白字符与字符参数
     *
     * @param str   字符串
     * @param chars 字符参数
     * @return 字符串
     */
    public static String ltrim(CharSequence str, String chars) {
        return StringUtils.ltrimBlank(str, chars.toCharArray());
    }

    /**
     * 删除字符串右侧的空白字符
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String rtrim(CharSequence str) {
        return StringUtils.rtrimBlank(str);
    }

    /**
     * 删除字符串右侧的空白字符与字符参数
     *
     * @param str   字符串
     * @param chars 字符参数
     * @return 字符串
     */
    public static String rtrim(CharSequence str, String chars) {
        return StringUtils.rtrimBlank(str, chars.toCharArray());
    }

    /**
     * 替换字符串中的内容
     *
     * @param str    字符串
     * @param oldStr 替换的字符串
     * @param newStr 替换后的字符串
     * @return 字符串
     */
    public static String replace(CharSequence str, String oldStr, String newStr) {
        return StringUtils.replaceAll(str, oldStr, newStr);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param begin 截取起始位置，从 0 开始
     * @return 字符串
     */
    public static String substr(CharSequence str, int begin) {
        return str.toString().substring(begin);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param begin 截取起始位置，从 0 开始
     * @param end   结束位置（不包括）
     * @return 字符串
     */
    public static String substr(CharSequence str, int begin, int end) {
        return str.toString().substring(begin, end);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static String substr(CharSequence str, long begin) {
        return str.toString().substring((int) begin);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static String substr(CharSequence str, long begin, long end) {
        return str.toString().substring((int) begin, (int) end);
    }

    /**
     * 判断字符串是否以指定前缀开头
     *
     * @param str         字符串
     * @param prefix      前缀
     * @param from        起始位置，从 0 开始
     * @param ignoreCase  true表示忽略大小写
     * @param ignoreBlank false表示忽略空白字符
     * @return 返回true表示是，false表示否
     */
    public static boolean startsWith(CharSequence str, CharSequence prefix, int from, boolean ignoreCase, boolean ignoreBlank) {
        return StringUtils.startsWith(str, prefix, from, ignoreCase, ignoreBlank);
    }

    /**
     * 判断字符串是否以指定前缀开头
     *
     * @param str        字符串
     * @param prefix     前缀
     * @param from       起始位置，从 0 开始
     * @param ignoreCase true表示忽略大小写
     * @return 返回true表示是，false表示否
     */
    public static boolean startsWith(CharSequence str, CharSequence prefix, int from, boolean ignoreCase) {
        return StringUtils.startsWith(str, prefix, from, ignoreCase, false);
    }

    /**
     * 判断字符串是否以指定前缀开头
     *
     * @param str    字符串
     * @param prefix 前缀
     * @param from   起始位置，从 0 开始
     * @return 返回true表示是，false表示否
     */
    public static boolean startsWith(CharSequence str, CharSequence prefix, int from) {
        return StringUtils.startsWith(str, prefix, from, false, false);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static boolean startsWith(CharSequence str, CharSequence prefix, long from, boolean ignoreCase, boolean ignoreBlank) {
        return StringUtils.startsWith(str, prefix, (int) from, ignoreCase, ignoreBlank);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static boolean startsWith(CharSequence str, CharSequence prefix, long from, boolean ignoreCase) {
        return StringUtils.startsWith(str, prefix, (int) from, ignoreCase, false);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static boolean startsWith(CharSequence str, CharSequence prefix, long from) {
        return StringUtils.startsWith(str, prefix, (int) from, false, false);
    }

    /**
     * 判断字符串是否以指定前缀开头
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 返回true表示是，false表示否
     */
    public static boolean startsWith(CharSequence str, CharSequence prefix) {
        return StringUtils.startsWith(str, prefix, 0, false, false);
    }

    /**
     * 将字符串使用空白字符分隔
     *
     * @param str 字符串
     * @return 字段集合
     */
    public static String[] split(CharSequence str) {
        return StringUtils.splitByBlank(str);
    }

    /**
     * 将字符串使用指定字符分隔
     *
     * @param str       字符串
     * @param delimiter 分隔字符
     * @return 字段集合
     */
    public static String[] split(CharSequence str, String delimiter) {
        return StringUtils.split(str, delimiter);
    }

    /**
     * 从字符串 str 的最左段移除字符串 prefix
     *
     * @param str    字符串
     * @param prefix 字符串
     * @return 移除后的字符串
     */
    public static String removePrefix(CharSequence str, CharSequence prefix) {
        return StringUtils.removePrefix(str, prefix);
    }
}
