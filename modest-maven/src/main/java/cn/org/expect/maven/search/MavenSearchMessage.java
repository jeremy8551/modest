package cn.org.expect.maven.search;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import cn.org.expect.util.MessageFormatter;

public class MavenSearchMessage {

    public final static String BUNDLE_NAME = "messages.MavenPluginBundle";

    public final static ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ROOT);

    public final static ResourceBundle BUNDLE_CN = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINESE);

    /** 函数式接口返回true，表示使用中文的资源文件 */
    private static Predicate<String> USE_CHINESE;

    private MavenSearchMessage() {
    }

    public static void setChineseCondition(Predicate<String> useChinese) {
        USE_CHINESE = useChinese;
    }

    public static String get(String key, Object... params) {
        String message = getMessage(key);
        return new MessageFormatter(MessageFormatter.Placeholder.NORMAL, message).fill(params);
    }

    private static String getMessage(String key) {
        if (USE_CHINESE != null && USE_CHINESE.test(key)) {
            return BUNDLE_CN.getString(key);
        } else {
            return BUNDLE.getString(key);
        }
    }
}
