package cn.org.expect.maven.plugin.plugOut;

import java.util.List;

public class DisablePlugin {

    private String groupId;

    private String artifactId;

    private List<String> goals;

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

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public String toString() {
        return "DisablePlugin{" + "groupId='" + groupId + '\'' + ", artifactId='" + artifactId + '\'' + ", goals=" + goals + '}';
    }
}
