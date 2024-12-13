package cn.org.expect.maven;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;

import cn.org.expect.util.StringUtils;
import com.intellij.CommonBundle;

public class MavenMessage {

    public final static String BUNDLE_NAME = "messages.MavenSearchPluginBundle";

    public final static ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ROOT);

    public final static ResourceBundle BUNDLE_CN = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINESE);

    /** 函数式接口返回true，表示使用中文的资源文件 */
    private static final Predicate<String> USE_CHINESE = (key) -> "取消".equals(CommonBundle.getCancelButtonText());

    private MavenMessage() {
    }

    /**
     * 返回国际化信息
     *
     * @param key  资源编号
     * @param args 参数数组
     * @return 字符串
     */
    public static String get(String key, Object... args) {
        String message = getResourceBundle(key);
        return StringUtils.replaceIndexHolder(message, args);
    }

    /**
     * 返回国际化资源信息
     *
     * @param key 资源编号
     * @return 国际化资源信息
     */
    public static String getResourceBundle(String key) {
        if (USE_CHINESE.test(key)) {
            return BUNDLE_CN.getString(key);
        } else {
            return BUNDLE.getString(key);
        }
    }

    /**
     * 判断属性是否存在
     *
     * @param key 属性名
     * @return 返回true表示属性存在，false表示属性不存在
     */
    public static boolean contains(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }

        Enumeration<String> keys = BUNDLE.getKeys();
        while (keys.hasMoreElements()) {
            if (key.equals(keys.nextElement())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回国际化资源编号集合
     */
    public static Set<String> getKeys() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        Enumeration<String> keys = BUNDLE.getKeys();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return set;
    }

    /**
     * 转为字符串
     *
     * @param text  文本信息
     * @param array 文本的参数
     * @return 字符串
     */
    public static String toString(String text, Object... array) {
        if (MavenMessage.contains(text)) {
            return MavenMessage.get(text, array);
        }

        if (StringUtils.isBlank(text)) {
            return text;
        }

        return StringUtils.replaceIndexHolder(text, array);
    }
}
