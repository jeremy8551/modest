package cn.org.expect.intellijidea.plugin.maven.search;

import cn.org.expect.intellijidea.plugin.maven.MavenFinder;

public class ExtraElement {
    private final MavenFinder mavenFinder;
    private final String groupId;
    private final String artifactId;

    public ExtraElement(MavenFinder mavenFinder, String groupId, String artifactId) {
        this.mavenFinder = mavenFinder;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public MavenFinder getMavenFinder() {
        return mavenFinder;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }
}
