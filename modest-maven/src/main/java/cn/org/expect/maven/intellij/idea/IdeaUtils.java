package cn.org.expect.maven.intellij.idea;

import cn.org.expect.maven.search.MavenSearchNotification;
import com.intellij.notification.NotificationType;

public class IdeaUtils {

    public static NotificationType toNotification(MavenSearchNotification type) {
        NotificationType nt;
        switch (type) {
            case NORMAL:
                nt = NotificationType.INFORMATION;
                break;

            case ERROR:
                nt = NotificationType.ERROR;
                break;

            default:
                nt = NotificationType.INFORMATION;
        }
        return nt;
    }
}
