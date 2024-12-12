package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.repository.ArtifactDownloader;
import cn.org.expect.maven.repository.local.LocalRepositoryNotFoundException;
import cn.org.expect.maven.repository.local.LocalRepositorySettings;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;

public class MavenSearchDownloadJob extends MavenSearchPluginJob {

    private final Artifact artifact;

    private volatile ArtifactDownloader downloader;

    public MavenSearchDownloadJob(Artifact artifact) {
        super("maven.search.job.download.artifact.description", artifact.toStandardString());
        this.artifact = Ensure.notNull(artifact);
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public int execute() throws Exception {
        MavenSearchPlugin plugin = this.getSearch();
        LocalRepositorySettings settings = plugin.getLocalRepositorySettings();
        File parent = plugin.getLocalRepository().getParent(this.artifact);
        FileUtils.createDirectory(parent, true);
        String id = plugin.getSettings().getDownloadWay();

        try {
            this.downloader = plugin.aware(plugin.getIoc().getBean(ArtifactDownloader.class, id));
            this.downloader.execute(this.artifact, parent, settings.isDownloadSourcesAutomatically(), settings.isDownloadDocsAutomatically(), settings.isDownloadAnnotationsAutomatically());
        } catch (LocalRepositoryNotFoundException e) {
            plugin.sendNotification(ArtifactSearchNotification.ERROR, "maven.search.error.cannot.found.local.repository");
        }
        return 0;
    }

    public void terminate() {
        super.terminate();
        if (this.downloader != null && !this.downloader.isTerminate()) {
            this.downloader.terminate();
        }
    }
}
