package cn.org.expect.maven.plugin.pom;

public interface MavenPom {

    /** 类名 */
    String CLASS_NAME = "ProjectMavenPom";

    /**
     * 返回构件的 groupId
     *
     * @return groupId
     */
    String getGroupID();

    /**
     * 返回构件的 artifactId
     *
     * @return artifactId
     */
    String getArtifactID();

    /**
     * 返回构件的 version
     *
     * @return version
     */
    String getVersion();
}
