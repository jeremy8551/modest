package cn.org.expect.maven.repository.clazz;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.repository.impl.SimpleArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.search.ArtifactSearchSettings;

@EasyBean(singleton = true)
public class SearchClassInRepositoryDatabaseEngine extends SimpleArtifactRepositoryDatabaseEngine {

    public SearchClassInRepositoryDatabaseEngine(ArtifactSearchSettings settings) {
        super(settings, "SEARCH_CLASS_IN_CENTRAL_PATTERN_TABLE.json", "SEARCH_CLASS_IN_CENTRAL_ARTIFACT_TABLE.json");
    }
}
