package cn.org.expect.ioc.internal;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 包扫描通配符
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/8
 */
public class ScanPattern {

    public final static char[] EXCLUDE_CHARS = {'^', '!', '！', StringUtils.toFullWidthChar('^')};

    /** 源名: !org.spring.* */
    private final String value;

    /** true表示排除模式 */
    private final boolean exclude;

    /** 包名前缀，如：org.spring */
    private final String prefix;

    /** 包名，如 !org.spring */
    private final String rule;

    public ScanPattern(String name) {
        this.value = StringUtils.trimBlank(Ensure.notBlank(name));
        this.exclude = StringUtils.inArray(this.value.charAt(0), EXCLUDE_CHARS);
        this.rule = StringUtils.rtrim(this.value, '.', '*', '?');
        String pattern = this.exclude ? StringUtils.ltrim(this.value, EXCLUDE_CHARS) : this.value; // 匹配模式，如：org.spring.*
        this.prefix = this.parse(pattern);
    }

    /**
     * 删除通配符中的匹配字符(* ?)
     *
     * @param str 通配符
     * @return 包名
     */
    public String parse(String str) {
        String[] array = StringUtils.split(str, '.');
        StringBuilder buf = new StringBuilder(str.length());
        for (int i = 0; i < array.length; i++) {
            String name = array[i];
            if (name.indexOf('*') != -1 || name.indexOf('?') != -1) {
                break;
            } else {
                buf.append(name).append('.');
            }
        }
        return StringUtils.rtrim(buf, '.');
    }

    /**
     * 扫描或排除
     *
     * @return true表示排除模式 false表示扫描模式
     */
    public boolean isExclude() {
        return this.exclude;
    }

    /**
     * 包名前缀
     *
     * @return 包名前缀，如：org.spring
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * 返回true表示包名长度为0
     *
     * @return 返回true表示包名是空的
     */
    public boolean isBlank() {
        return this.prefix.length() == 0;
    }

    /**
     * 包名，如 !org.spring
     *
     * @return 返回扫描规则
     */
    public String getRule() {
        return rule;
    }

    /**
     * 判断是否包含了参数 {@code easyPackage} 指定的范围
     *
     * @param pattern 包名
     * @return 返回true表示已包含 false表示未包含
     */
    public boolean contains(ScanPattern pattern) {
        return (pattern.isExclude() == this.isExclude()) && pattern.getPrefix().startsWith(this.getPrefix());
    }

    public boolean equals(Object obj) {
        if (obj instanceof ScanPattern) {
            ScanPattern e = (ScanPattern) obj;
            return e.getRule().equals(this.getRule());
        }
        return false;
    }

    public String toString() {
        return this.value;
    }
}
