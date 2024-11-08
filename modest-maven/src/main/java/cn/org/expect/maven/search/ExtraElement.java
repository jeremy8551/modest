package cn.org.expect.maven.search;

public class ExtraElement {
    private final MavenSearch mavenFinder;
    private final String groupId;
    private final String artifactId;

    public ExtraElement(MavenSearch mavenFinder, String groupId, String artifactId) {
        this.mavenFinder = mavenFinder;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public MavenSearch getMavenFinder() {
        return mavenFinder;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }
}
