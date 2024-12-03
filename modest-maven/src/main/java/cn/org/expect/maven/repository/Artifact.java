package cn.org.expect.maven.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.org.expect.util.NetUtils;
import cn.org.expect.util.StringComparator;
import cn.org.expect.util.StringUtils;

/**
 * Maven仓库工件信息
 */
public interface Artifact {

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

    /**
     * 判断工件的 groupId 与 artifactId 是否相等
     *
     * @param artifact 工件
     * @return 返回true表示相等，false表示不等
     */
    default boolean equalsId(Artifact artifact) {
        return artifact != null && StringComparator.compareTo(this.getGroupId(), artifact.getGroupId()) == 0 && StringComparator.compareTo(this.getArtifactId(), artifact.getArtifactId()) == 0;
    }

    /**
     * 判断工件的 groupId、artifactId、version 是否相等
     *
     * @param artifact 工件
     * @return 返回true表示相等，false表示不等
     */
    default boolean equalsVersion(Artifact artifact) {
        return this.equalsId(artifact) && StringComparator.compareTo(this.getVersion(), artifact.getVersion()) == 0;
    }

    /**
     * 转为标准的 groupId:artifactId:version 格式
     *
     * @return 字符串
     */
    default String toStandardString() {
        return this.getGroupId() + ":" + this.getArtifactId() + ":" + this.getVersion();
    }

    /**
     * 使用 Groovy 的Gradle依赖
     *
     * @return Gradle依赖
     */
    default String toGradleGroovyDependency() {
        String text = "";
        text += "implementation '";
        text += this.getGroupId();
        text += ":";
        text += this.getArtifactId();
        text += ":";
        text += this.getVersion();
        text += "'";
        return text;
    }

    /**
     * 使用 Kotlin 的Gradle依赖
     *
     * @return Gradle依赖
     */
    default String toGradleKotlinDependency() {
        String text = "";
        text += "implementation(\"";
        text += this.getGroupId();
        text += ":";
        text += this.getArtifactId();
        text += ":";
        text += this.getVersion();
        text += "\")";
        return text;
    }

    /**
     * 使用 Groovy 的Gradle依赖
     *
     * @return Gradle依赖
     */
    default String toGradlePluginGroovyDependency() {
        String text = "";
        text += "id '";
        text += this.getArtifactId();
        text += "' version '";
        text += this.getVersion();
        text += "'";
        return text;
    }

    /**
     * 使用 Kotlin 的Gradle依赖
     *
     * @return Gradle依赖
     */
    default String toGradlePluginKotlinDependency() {
        String text = "";
        text += "id(\"";
        text += this.getArtifactId();
        text += "\") version \"";
        text += this.getVersion();
        text += "\"";
        return text;
    }

    default String toURI(String url, Artifact artifact) {
        List<String> list = new ArrayList<>();
        list.add(url);
        StringUtils.split(artifact.getGroupId(), '.', list);
        list.add(artifact.getArtifactId());
        list.add(artifact.getVersion());
        return NetUtils.joinUri(list.toArray(new String[0]));
    }
}
