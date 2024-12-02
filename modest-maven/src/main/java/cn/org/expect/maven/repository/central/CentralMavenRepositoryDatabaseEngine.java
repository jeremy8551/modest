package cn.org.expect.maven.repository.central;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.repository.impl.SimpleArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.search.ArtifactSearchSettings;

@EasyBean(value = "central", singleton = true)
public class CentralMavenRepositoryDatabaseEngine extends SimpleArtifactRepositoryDatabaseEngine {

    public CentralMavenRepositoryDatabaseEngine(ArtifactSearchSettings settings) {
        super(settings, "CENTRAL_PATTERN_TABLE.json", "CENTRAL_ARTIFACT_TABLE.json");
    }
}
