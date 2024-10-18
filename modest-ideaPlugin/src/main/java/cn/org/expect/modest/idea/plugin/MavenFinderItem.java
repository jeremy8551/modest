package cn.org.expect.modest.idea.plugin;

import java.util.concurrent.atomic.AtomicLong;

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

    public String getRepositoryUrl() {
        StringBuilder buf = new StringBuilder();
        buf.append("https://repo1.maven.org/maven2/");
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
        return this.groupId + ":" + this.artifact + ":" + this.version + ", id=" + this.id;
    }

    public String getPresentableText() {
        return this.groupId + ":" + this.artifact + ":" + this.version;
    }

    public String getLocationString() {
        return this.type;
    }

    public static class Blank extends MavenFinderItem {

        public Blank() {
            super();
        }

        public String getRepositoryUrl() {
            return "https://repo1.maven.org/maven2/";
        }

        public String getPresentableText() {
            return toString();
        }

        public String getLocationString() {
            return toString();
        }

        public boolean equals(Object o) {
            return false;
        }

        public String toString() {
            return "" + this.getId();
        }
    }
}


