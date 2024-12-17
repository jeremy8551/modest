package cn.org.expect.maven.repository.gradle;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.repository.impl.SimpleArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.search.ArtifactSearchSettings;

@EasyBean(singleton = true)
public class GradlePluginRepositoryDatabaseEngine extends SimpleArtifactRepositoryDatabaseEngine {

    public GradlePluginRepositoryDatabaseEngine(ArtifactSearchSettings settings) {
        super(settings, "GRADLE_PATTERN_TABLE.json", "GRADLE_ARTIFACT_TABLE.json");
    }
}
