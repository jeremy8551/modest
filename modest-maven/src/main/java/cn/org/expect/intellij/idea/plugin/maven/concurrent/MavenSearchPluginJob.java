package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.concurrent.MavenSearchJob;

public abstract class MavenSearchPluginJob extends MavenSearchJob {

    public MavenSearchPluginJob() {
        super();
    }

    public MavenSearchPlugin getSearch() {
        return (MavenSearchPlugin) super.getSearch();
    }
}
