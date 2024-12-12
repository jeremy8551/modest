package cn.org.expect.maven.impl;

import java.util.Date;

import cn.org.expect.maven.Artifact;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringComparator;
import cn.org.expect.util.StringUtils;

public class SimpleArtifact implements Artifact {

    /** 工件ID */
    private String artifactId;

    /** 域名 */
    private String groupId;

    /** 版本号 */
    private String version;

    /** 打包类型：jar、pom、war、maven-plugin 等 */
    private String type;

    /** 上传仓库的时间 */
    private Date timestamp;

    /** 版本数 */
    private final int versionCount;

    public SimpleArtifact(String groupId, String artifactId, String version, String type, Date timestamp, int versionCount) {
        this.artifactId = StringUtils.trimBlank(artifactId);
        this.groupId = StringUtils.trimBlank(groupId);
        this.version = StringUtils.trimBlank(version);
        this.type = StringUtils.trimBlank(type);
        this.timestamp = timestamp;
        this.versionCount = versionCount;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public int getVersionCount() {
        return versionCount;
    }

    public String getType() {
        return type;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Artifact) {
            Artifact artifact = (Artifact) obj;
            return StringComparator.compareTo(this.groupId, artifact.getGroupId()) == 0 //
                    && StringComparator.compareTo(this.artifactId, artifact.getArtifactId()) == 0 //
                    && StringComparator.compareTo(this.version, artifact.getVersion()) == 0 //
                    && StringComparator.compareTo(this.type, artifact.getType()) == 0 //
                    ;
        }
        return false;
    }

    public String toString() {
        return this.groupId + ":" + this.artifactId + ":" + this.version + ", time=" + Dates.format19(this.getTimestamp()) + ", " + this.getVersionCount();
    }
}
