package cn.org.expect.maven.repository.gradle;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.repository.impl.SimpleArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.search.ArtifactSearchSettings;

@EasyBean(value = "gradle", singleton = true)
public class GradlePluginMavenRepositoryDatabaseEngine extends SimpleArtifactRepositoryDatabaseEngine {

    public GradlePluginMavenRepositoryDatabaseEngine(ArtifactSearchSettings settings) {
        super(settings, "GRADLE_PATTERN_TABLE.json", "GRADLE_ARTIFACT_TABLE.json");
    }
}
