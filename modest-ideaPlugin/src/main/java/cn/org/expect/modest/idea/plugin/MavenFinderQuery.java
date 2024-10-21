package cn.org.expect.modest.idea.plugin;

import java.io.IOException;
import java.util.List;

public interface MavenFinderQuery {

    String getRepositoryUrl();

    List<MavenFinderItem> execute(String pattern) throws IOException;

    List<MavenFinderItem> execute(String groupId, String artifactId) throws IOException;
}
