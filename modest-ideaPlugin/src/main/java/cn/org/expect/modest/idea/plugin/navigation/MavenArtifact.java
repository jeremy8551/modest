package cn.org.expect.modest.idea.plugin.navigation;

import java.util.Date;

import cn.org.expect.util.Dates;
import cn.org.expect.util.StringComparator;

public class MavenArtifact {

    private String artifact;
    private String groupId;
    private String version;
    private String type;
    private long timestamp;
    private int versionCount;
    private String repositoryUrl;

    /** true表示折叠版本列表 */
    private volatile boolean fold;

    public MavenArtifact() {
        this("", "", "", "", 0, -1);
    }

    public MavenArtifact(String groupId, String artifact, String version, String type, long timestamp, int versionCount) {
        this.artifact = artifact;
        this.groupId = groupId;
        this.version = version;
        this.type = type;
        this.timestamp = timestamp;
        this.versionCount = versionCount;
        this.fold = true;
    }

    public String getArtifactId() {
        return artifact;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    public Date getTimestamp() {
        return new Date(this.timestamp);
    }

    public int getVersionCount() {
        return versionCount;
    }

    public String getType() {
        return type;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MavenArtifact) {
            MavenArtifact artifact = (MavenArtifact) obj;
            return StringComparator.compareTo(this.groupId, artifact.getGroupId()) == 0 //
                    && StringComparator.compareTo(this.artifact, artifact.getArtifactId()) == 0 //
                    && StringComparator.compareTo(this.version, artifact.getVersion()) == 0 //
                    && StringComparator.compareTo(this.type, artifact.getType()) == 0 //
                    ;
        }
        return false;
    }

    public String toString() {
        return this.groupId + ":" + this.artifact + ":" + this.version + ", time=" + Dates.format19(this.getTimestamp()) + ", " + this.getVersionCount();
    }
}


