package cn.org.expect.maven.entity;

public class DisablePlugin {

    private String groupId;

    private String artifactId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String toString() {
        return "HidePlugin{" + "groupId='" + groupId + '\'' + ", artifactId='" + artifactId + '\'' + '}';
    }
}
