package cn.org.expect.maven.pom.impl;

import cn.org.expect.maven.pom.Parent;
import cn.org.expect.util.StringUtils;

public class SimpleParent implements Parent {
    private String artifactId;
    private String groupId;
    private String version;

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = StringUtils.trimBlank(artifactId);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = StringUtils.trimBlank(groupId);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = StringUtils.trimBlank(version);
    }

    public String toString() {
        return this.groupId + ":" + this.artifactId + ":" + this.version;
    }
}
