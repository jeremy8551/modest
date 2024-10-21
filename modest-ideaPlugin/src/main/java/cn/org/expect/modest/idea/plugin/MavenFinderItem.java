package cn.org.expect.modest.idea.plugin;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

public class MavenFinderItem {

    private final static AtomicLong NUMBER = new AtomicLong(0);

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
    private String repositoryUrl;

    public MavenFinderItem() {
        this("", "", "", "", "", 0, 0, new String[0], new String[0]);
    }

    public MavenFinderItem(String groupId, String artifact, String version, String type, String repository, long timestamp, int versionCount, String[] text, String[] ec) {
        this.id = NUMBER.incrementAndGet();
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

    public String getArtifact() {
        return artifact;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    public String getRepository() {
        return this.repository;
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

    public String[] getText() {
        return text;
    }

    public String[] getEc() {
        return ec;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public String getNavigateUrl() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.repositoryUrl);
        buf.append(this.groupId.replace('.', '/'));
        buf.append('/');
        buf.append(this.artifact.replace('.', '/'));
        return buf.toString();
    }

    public long getId() {
        return id;
    }

    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass()) && ((MavenFinderItem) o).id == this.id;
    }

    public String toString() {
        return "id=" + this.id + ", " + this.groupId + ":" + this.artifact + ":" + this.version + ", time: " + Dates.format21(this.getTimestamp()) + ", " + this.getVersionCount();
    }

    public String getPresentableText() {
        if (StringUtils.isBlank(this.groupId) && StringUtils.isBlank(this.artifact) && StringUtils.isBlank(this.version)) {
            return "";
        } else {
            return this.groupId + ":" + this.artifact + ":" + this.version;
        }
    }

    public String getLocationString() {
        return this.type;
    }
}


