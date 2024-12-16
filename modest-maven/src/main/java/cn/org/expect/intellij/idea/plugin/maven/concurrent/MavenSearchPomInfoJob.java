package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.Artifact;

public class MavenSearchPomInfoJob extends MavenSearchArtifactJob {

    public MavenSearchPomInfoJob(Artifact artifact) {
        super(artifact, "maven.search.job.search.pom.info.description");
    }

    public int execute() throws Exception {
        MavenSearchPlugin plugin = this.getSearch();
        plugin.getPomInfoRepository().query(plugin, this.getArtifact());
        plugin.asyncDisplay();
        return 0;
    }
}
