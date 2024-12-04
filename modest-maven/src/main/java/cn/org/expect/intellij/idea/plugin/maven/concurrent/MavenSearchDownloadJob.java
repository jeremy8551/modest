package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactDownloader;
import cn.org.expect.maven.repository.local.LocalMavenRepositorySettings;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;

public class MavenSearchDownloadJob extends MavenSearchPluginJob implements EDTJob {
    private final static Log log = LogFactory.getLog(MavenSearchDownloadJob.class);

    private final Artifact artifact;

    public MavenSearchDownloadJob(Artifact artifact) {
        super();
        this.artifact = Ensure.notNull(artifact);
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public int execute() throws Exception {
        MavenSearchPlugin plugin = this.getSearch();
        File parent = plugin.getLocalRepository().getParent(this.artifact);
        FileUtils.createDirectory(parent, true);
        LocalMavenRepositorySettings settings = plugin.getLocalRepositorySettings();

        String id = plugin.getSettings().getDownloadWay();
        ArtifactDownloader downloader = plugin.aware(plugin.getEasyContext().getBean(ArtifactDownloader.class, id));
        downloader.execute(this.artifact, parent, settings.isDownloadSourcesAutomatically(), settings.isDownloadSourcesAutomatically(), settings.isDownloadAnnotationsAutomatically());
        return 0;
    }
}
