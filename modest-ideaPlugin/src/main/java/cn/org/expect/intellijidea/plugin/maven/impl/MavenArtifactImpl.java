package cn.org.expect.intellijidea.plugin.maven.impl;

import java.util.Date;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringComparator;

public class MavenArtifactImpl implements MavenArtifact {

    private String artifact;
    private String groupId;
    private String version;
    private String type;
    private long timestamp;
    private int versionCount;

    /** true表示折叠版本列表 */
    private volatile boolean fold;

    public MavenArtifactImpl() {
        this("", "", "", "", 0, -1);
    }

    public MavenArtifactImpl(String groupId, String artifact, String version, String type, long timestamp, int versionCount) {
        this.artifact = artifact;
        this.groupId = groupId;
        this.version = version;
        this.type = type;
        this.timestamp = timestamp;
        this.versionCount = versionCount;
        this.fold = true;
    }

    @Override
    public String getArtifactId() {
        return artifact;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Date getTimestamp() {
        return new Date(this.timestamp);
    }

    @Override
    public int getVersionCount() {
        return versionCount;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isFold() {
        return fold;
    }

    @Override
    public boolean isUnfold() {
        return !this.fold;
    }

    @Override
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

    @Override
    public String toString() {
        return this.groupId + ":" + this.artifact + ":" + this.version + ", time=" + Dates.format19(this.getTimestamp()) + ", " + this.getVersionCount();
    }
}


