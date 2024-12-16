package cn.org.expect.maven;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.org.expect.util.NetUtils;
import cn.org.expect.util.StringUtils;

/**
 * 仓库工件信息
 */
public interface Artifact {

    /**
     * 工件的扩展名数组
     */
    String[] EXTENSION_TYPES = new String[]{"jar", "pom", "war", "ear", "zip", "aar", "tar.gz", "xml", "module"};

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
     * 判断工件是否相等
     *
     * @param groupId    域名
     * @param artifactId 工件ID
     * @param version    版本号
     * @return 返回true表示相等，false表示不等
     */
    default boolean equals(String groupId, String artifactId, String version) {
        return this.getGroupId().equals(groupId) && this.getArtifactId().equals(artifactId) && this.getVersion().equals(version);
    }

    /**
     * 判断工件是否相等
     *
     * @param groupId    域名
     * @param artifactId 工件ID
     * @return 返回true表示相等，false表示不等
     */
    default boolean equals(String groupId, String artifactId) {
        return this.getGroupId().equals(groupId) && this.getArtifactId().equals(artifactId);
    }

    /**
     * 转为标准的 groupId:artifactId:version 格式
     *
     * @return 字符串
     */
    default String toMavenId() {
        return this.getGroupId() + ":" + this.getArtifactId() + ":" + this.getVersion();
    }

    /**
     * 生成 Maven POM 的依赖信息
     *
     * @return 依赖信息
     */
    default String toMavenPomDependency() {
        String text = "";
        text += "<groupId>";
        text += this.getGroupId();
        text += "</groupId>\n";
        text += "<artifactId>";
        text += this.getArtifactId();
        text += "</artifactId>\n";
        text += "<version>";
        text += this.getVersion();
        text += "</version>\n";
        return text;
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

    /**
     * 将工件转为 Http URI
     *
     * @param url 下载工件的仓库地址
     * @return 地址
     */
    default String toURI(String url) {
        List<String> list = new ArrayList<>();
        list.add(url);
        StringUtils.split(this.getGroupId(), '.', list);
        list.add(this.getArtifactId());
        list.add(this.getVersion());
        return NetUtils.joinUri(list.toArray(new String[0]));
    }

    /**
     * 返回下载文件名的集合
     *
     * @return 文件名集合
     */
    default List<String> getFilenames() {
        List<String> result = new ArrayList<>();
        String filename = this.getArtifactId() + "-" + this.getVersion();
        for (String ext : Artifact.EXTENSION_TYPES) {
            result.add(filename + "." + ext);
        }
        result.add(filename + "-javadoc.jar");
        result.add(filename + "-sources.jar");
        result.add(filename + "-tests.jar");
        return result;
    }
}
