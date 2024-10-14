package cn.org.expect.modest.idea.plugin;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import cn.org.expect.util.StringUtils;

public final class MavenFinderItem {

    private final static AtomicLong no = new AtomicLong(0);

    private long id;
    private String artifact;
    private String groupId;
    private String version;
    private String type;
    private String repository;
    private long timestamp;
    private int versionCount;
    private String[] text;
    private String[] ec;

    public MavenFinderItem(String artifact, String groupId, String version, String type, String repository, long timestamp, int versionCount, String[] text, String[] ec) {
        this.id = no.addAndGet(1);
        this.artifact = artifact;
        this.groupId = groupId;
        this.version = version;
        this.type = type;
        this.repository = repository;
        this.timestamp = timestamp;
        this.versionCount = versionCount;
        this.text = text;
        this.ec = ec;
    }

    public void clone(MavenFinderItem item) {
        this.artifact = item.artifact;
        this.groupId = item.groupId;
        this.version = item.version;
        this.type = item.type;
        this.repository = item.repository;
        this.timestamp = item.timestamp;
        this.versionCount = item.versionCount;
        this.text = item.text;
        this.ec = item.ec;
    }

    public String getArtifact() {
        return artifact;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getRepository() {
        return repository;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getVersionCount() {
        return versionCount;
    }

    public String[] getText() {
        return text;
    }

    public String[] getEc() {
        return ec;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof MavenFinderItem) {
            MavenFinderItem item = (MavenFinderItem) o;
            return item.id == this.id;
        }

        return false;
    }

    public int hashCode() {
        return Objects.hash(artifact, groupId, version, type, repository, timestamp, versionCount, Arrays.hashCode(text), Arrays.hashCode(ec));
    }

    public String toString() {
        return this.groupId + ":" + this.artifact + ":" + this.version + StringUtils.repeat(' ', 10) + this.type + StringUtils.repeat(' ', 10) + this.repository;
    }

    public String getPresentableText() {
        return this.groupId + ":" + this.artifact + ":" + this.version;
    }

    public String getLocationString() {
        return this.type;
    }
}
