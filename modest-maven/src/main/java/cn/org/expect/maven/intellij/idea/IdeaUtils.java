package cn.org.expect.maven.intellij.idea;

import javax.swing.*;

import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchNotification;
import com.intellij.notification.NotificationType;

public class IdeaUtils {

    public static NotificationType toNotification(MavenSearchNotification type) {
        if (type == null) {
            return NotificationType.INFORMATION;
        }

        if (type == MavenSearchNotification.ERROR) {
            return NotificationType.ERROR;
        }

        return NotificationType.INFORMATION;
    }

    public static Icon getIcon(MavenSearchAdvertiser type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case NORMAL:
                return MavenPluginIcon.BOTTOM;

            case RUNNING:
                return MavenPluginIcon.BOTTOM_WAITING;

            case ERROR:
                return MavenPluginIcon.BOTTOM_ERROR;

            default:
                throw new UnsupportedOperationException(type.name());
        }
    }
}
