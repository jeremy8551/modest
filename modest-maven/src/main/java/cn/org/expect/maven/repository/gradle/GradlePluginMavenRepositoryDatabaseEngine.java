package cn.org.expect.maven.repository.gradle;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.repository.impl.SimpleMavenRepositoryDatabaseEngine;
import cn.org.expect.maven.search.ArtifactSearchSettings;

@EasyBean(value = "gradle", singleton = true)
public class GradlePluginMavenRepositoryDatabaseEngine extends SimpleMavenRepositoryDatabaseEngine {

    public GradlePluginMavenRepositoryDatabaseEngine(ArtifactSearchSettings settings) {
        super(settings, "GRADLE_PATTERN_TABLE.json", "GRADLE_ARTIFACT_TABLE.json");
    }
}
