package cn.org.expect.modest.idea.plugin;

import java.io.IOException;
import java.util.List;

public interface MavenFinderQuery {

    List<MavenFinderItem> execute(String pattern) throws IOException;

    String getRepositoryUrl();

}
