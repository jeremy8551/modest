package cn.org.expect.maven.repository;

import java.util.Comparator;
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

    Comparator<Date> TIMESTAMP_COMPARATOR = (o1, o2) -> {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            return o1.compareTo(o2);
        }
    };
}
