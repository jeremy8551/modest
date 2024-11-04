package cn.org.expect.intellijidea.plugin.maven.search;

import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;

public interface JsonResult {
    
    List<MavenArtifact> getList();

    int getNumFound();

    int getStart();
}
