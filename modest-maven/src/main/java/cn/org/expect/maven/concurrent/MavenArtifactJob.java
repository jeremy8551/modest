package cn.org.expect.maven.concurrent;

import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.ArtifactGetter;

public abstract class MavenArtifactJob extends MavenJob implements ArtifactGetter {

    private final Artifact artifact;

    public MavenArtifactJob(Artifact artifact, String description, Object... descriptionParams) {
        super(description, descriptionParams);
        this.artifact = artifact;
    }

    public Artifact getArtifact() {
        return artifact;
    }
}
