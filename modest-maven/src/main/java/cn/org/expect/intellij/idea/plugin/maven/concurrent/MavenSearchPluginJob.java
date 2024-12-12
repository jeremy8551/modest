package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.concurrent.ArtifactSearchJob;

public abstract class MavenSearchPluginJob extends ArtifactSearchJob {

    public MavenSearchPluginJob(String description, Object... descriptionParams) {
        super(description, descriptionParams);
    }

    public MavenSearchPlugin getSearch() {
        return (MavenSearchPlugin) super.getSearch();
    }
}
