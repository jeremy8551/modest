package cn.org.expect.modest.idea.plugin.db;

import java.io.IOException;
import java.util.List;

import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;

public interface MavenFinderQuery {

    String getRepositoryUrl();

    List<MavenArtifact> execute(String pattern) throws IOException;

    List<MavenArtifact> execute(String groupId, String artifactId) throws IOException;
}
