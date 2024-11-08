package cn.org.expect.maven.search;

public class ExtraElement {

    private final MavenSearch search;
    private final String groupId;
    private final String artifactId;

    public ExtraElement(MavenSearch search, String groupId, String artifactId) {
        this.search = search;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public MavenSearch getSearch() {
        return search;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }
}
