package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.maven.Artifact;

public abstract class MavenSearchArtifactJob extends MavenSearchPluginJob {

    private final Artifact artifact;

    public MavenSearchArtifactJob(Artifact artifact, String description, Object... descriptionParams) {
        super(description, descriptionParams);
        this.artifact = artifact;
    }

    public Artifact getArtifact() {
        return artifact;
    }
}
