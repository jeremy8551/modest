package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.Artifact;

public class MavenSearchPomInfoJob extends MavenSearchPluginJob implements EDTJob {

    protected final Artifact artifact;

    public MavenSearchPomInfoJob(Artifact artifact) {
        super("maven.search.job.search.pom.info.description");
        this.artifact = artifact;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public int execute() throws Exception {
        MavenSearchPlugin plugin = this.getSearch();
        plugin.getPomInfoRepository().query(plugin, artifact);
        plugin.display();
        return 0;
    }
}
