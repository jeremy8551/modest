package cn.org.expect.maven.search;

import java.util.Locale;
import java.util.ResourceBundle;

import cn.org.expect.util.MessageFormatter;
import com.intellij.CommonBundle;

public class MavenSearchMessage {

    public final static ResourceBundle BUNDLE = ResourceBundle.getBundle("messages.MavenPluginBundle", Locale.ROOT);

    public final static ResourceBundle BUNDLE_CN = ResourceBundle.getBundle("messages.MavenPluginBundle", Locale.CHINESE);

    private MavenSearchMessage() {
    }

    public static String get(String key, Object... params) {
        String message = getMessage(key);
        return new MessageFormatter(MessageFormatter.Placeholder.NORMAL, message).fill(params);
    }

    private static String getMessage(String key) {
        if ("取消".equals(CommonBundle.getCancelButtonText())) {
            return BUNDLE_CN.getString(key);
        } else {
            return BUNDLE.getString(key);
        }
    }
}
