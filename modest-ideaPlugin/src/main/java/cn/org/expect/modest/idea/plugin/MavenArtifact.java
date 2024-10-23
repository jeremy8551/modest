package cn.org.expect.modest.idea.plugin;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import cn.org.expect.util.Dates;

public class MavenArtifact {

    private final static AtomicLong NUMBER = new AtomicLong(0);

    private long id;
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
        this("", "", "", "", 0, 0);
    }

    public MavenArtifact(String groupId, String artifact, String version, String type, long timestamp, int versionCount) {
        this.id = NUMBER.incrementAndGet();
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

    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass()) && ((MavenArtifact) o).id == this.id;
    }

    public String toString() {
        return "id=" + this.id + ", " + this.groupId + ":" + this.artifact + ":" + this.version + ", time=" + Dates.format19(this.getTimestamp()) + ", " + this.getVersionCount();
    }
}


