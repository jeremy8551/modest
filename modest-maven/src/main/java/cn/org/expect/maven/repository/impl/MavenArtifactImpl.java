package cn.org.expect.maven.repository.impl;

import java.util.Date;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringComparator;

public class MavenArtifactImpl implements MavenArtifact {

    private String artifactId;
    private String groupId;
    private String version;
    private String type;
    private Date timestamp;
    private int versionCount;

    /** true表示折叠，false表示展开 */
    private volatile boolean fold;

    public MavenArtifactImpl(String groupId, String artifactId, String version, String type, Date timestamp, int versionCount) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
        this.type = type;
        this.timestamp = timestamp;
        this.versionCount = versionCount;
        this.fold = true;
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

    public boolean isFold() {
        return fold;
    }

    public boolean isUnfold() {
        return !this.fold;
    }

    public void setFold(boolean fold) {
        this.fold = fold;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MavenArtifact) {
            MavenArtifact artifact = (MavenArtifact) obj;
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


