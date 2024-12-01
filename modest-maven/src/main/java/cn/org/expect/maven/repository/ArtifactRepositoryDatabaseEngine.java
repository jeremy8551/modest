package cn.org.expect.maven.repository;

import java.util.Map;

public interface ArtifactRepositoryDatabaseEngine {
    
    Map<String, ArtifactSearchResult> getPattern();

    Map<String, Map<String, ArtifactSearchResult>> getArtifact();

    void clear();

    void save();
}
