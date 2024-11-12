package cn.org.expect.maven.search;

import cn.org.expect.util.MessageFormatter;
import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;

public class MavenSearchMessage {

    public final static MessageFormatter SEARCHING = new MessageFormatter("Search in Maven Repository ..");

    public final static MessageFormatter SEARCHING_EXTRA = new MessageFormatter("Search {}:{} in Maven Repository ..");

    public final static MessageFormatter SEARCHING_PATTERN = new MessageFormatter("Search '{}' in Maven Repository ..");

    public final static MessageFormatter FAIL_SEND_REQUEST = new MessageFormatter("Failed to send query request to Maven Repository!");

    public final static MessageFormatter REMOTE_SEARCH_RESULT = new MessageFormatter("Found: {}, Display: {}");

    public final static MessageFormatter NOTHING_FOUND = new MessageFormatter("Nothing found.");

    public final static MessageFormatter START_THREAD = new MessageFormatter("{} start ..");

    public final static MessageFormatter DETECTED_IDEA_UI_COMPONENT = new MessageFormatter("{} finish!");

    private final static MavenPluginDynamicBundle BUNDLE = new MavenPluginDynamicBundle("messages.MavenPluginBundle");

    private MavenSearchMessage() {
    }

    public static String get(String key, Object... params) {
        return BUNDLE.getMessage(key, params);
    }

    private static class MavenPluginDynamicBundle extends DynamicBundle {
        protected MavenPluginDynamicBundle(@NotNull String pathToBundle) {
            super(pathToBundle);
        }
    }
}
