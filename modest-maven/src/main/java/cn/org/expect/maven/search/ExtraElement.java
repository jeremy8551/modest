package cn.org.expect.maven.search;

public class ExtraElement {

    private final SearchOperation mavenFinder;
    private final String groupId;
    private final String artifactId;

    public ExtraElement(SearchOperation mavenFinder, String groupId, String artifactId) {
        this.mavenFinder = mavenFinder;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public SearchOperation getMavenFinder() {
        return mavenFinder;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }
}
