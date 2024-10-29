package cn.org.expect.intellijidea.plugin.maven;

import java.util.Date;

/**
 * Maven仓库工件信息
 */
public interface MavenArtifact {

    /**
     * 返回域名
     *
     * @return 域名
     */
    String getGroupId();

    /**
     * 返回工件ID
     *
     * @return 工件ID
     */
    String getArtifactId();

    /**
     * 返回版本号
     *
     * @return 版本号
     */
    String getVersion();

    /**
     * 返回工件上传的时间戳
     *
     * @return 时间戳
     */
    Date getTimestamp();

    /**
     * 返回工件的版本记录数
     *
     * @return 记录数
     */
    int getVersionCount();

    /**
     * 返回工件类型
     *
     * @return 工件类型：pom、jar、maven-plugin
     */
    String getType();

    /**
     * 判断是否折叠
     *
     * @return 返回true表示折叠
     */
    boolean isFold();

    /**
     * 判断是否展开
     *
     * @return 返回true表示展开
     */
    boolean isUnfold();

    /**
     * 设置是否折叠
     *
     * @param fold true表示折叠 false表示展开
     */
    void setFold(boolean fold);
}
