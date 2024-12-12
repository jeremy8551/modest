package cn.org.expect.maven.search;

import com.intellij.notification.NotificationType;

/**
 * 通知类型
 */
public enum ArtifactSearchNotification {

    /** 正常通知 */
    NORMAL,

    /** 错误通知 */
    ERROR;

    /**
     * 转为 Idea 编辑器的通知类型
     *
     * @param type 通知类型
     * @return Idea 编辑器的通知类型
     */
    public static NotificationType toIdea(ArtifactSearchNotification type) {
        if (type == null) {
            return NotificationType.INFORMATION;
        }

        if (type == ArtifactSearchNotification.ERROR) {
            return NotificationType.ERROR;
        }

        return NotificationType.INFORMATION;
    }
}
