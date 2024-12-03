package cn.org.expect.maven.search;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import cn.org.expect.util.MessageFormatter;
import com.intellij.CommonBundle;

public class ArtifactSearchMessage {

    public final static String BUNDLE_NAME = "messages.MavenSearchPluginBundle";

    public final static ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ROOT);

    public final static ResourceBundle BUNDLE_CN = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINESE);

    /** 函数式接口返回true，表示使用中文的资源文件 */
    private static final Predicate<String> USE_CHINESE = (key) -> "取消".equals(CommonBundle.getCancelButtonText());

    private ArtifactSearchMessage() {
    }

    public static String get(String key, Object... params) {
        String message = getMessage(key);
        return new MessageFormatter(MessageFormatter.Placeholder.NORMAL, message).fill(params);
    }

    /**
     * 根据属性值查询对应的 key
     *
     * @param value 属性值
     * @return key
     */
    public static String getKey(String value) {
        Enumeration<String> keys = BUNDLE.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (value.equals(BUNDLE.getString(key))) {
                return key;
            }
        }
        throw new UnsupportedOperationException(value);
    }

    /**
     * 返回选项名
     *
     * @return 选项名
     */
    public static String getOptionName(String repositoryId) {
        return ArtifactSearchMessage.get("maven.search.repository." + repositoryId + ".id");
    }

    private static String getMessage(String key) {
        if (USE_CHINESE.test(key)) {
            return BUNDLE_CN.getString(key);
        } else {
            return BUNDLE.getString(key);
        }
    }
}
