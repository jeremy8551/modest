package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.Artifact;

public class MavenSearchPomJob extends MavenSearchArtifactJob {

    public MavenSearchPomJob(Artifact artifact) {
        super(artifact, "maven.search.job.search.pom.info.description");
    }

    public int execute() throws Exception {
        MavenSearchPlugin plugin = this.getSearch();
        plugin.getPomRepository().query(plugin, this.getArtifact());
        plugin.asyncDisplay();
        return 0;
    }
}
