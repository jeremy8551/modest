package cn.org.expect.maven.search;

public class SearchElementExtra {

    private final MavenSearch search;
    private final String groupId;
    private final String artifactId;

    public SearchElementExtra(MavenSearch search, String groupId, String artifactId) {
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
