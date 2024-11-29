package cn.org.expect.maven.repository;

import java.util.Map;

public interface MavenRepositoryDatabaseEngine {
    
    Map<String, MavenSearchResult> getPattern();

    Map<String, Map<String, MavenSearchResult>> getArtifact();

    void clear();

    void save();
}
