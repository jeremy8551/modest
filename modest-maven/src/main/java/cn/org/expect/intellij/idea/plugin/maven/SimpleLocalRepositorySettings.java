package cn.org.expect.intellij.idea.plugin.maven;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.listener.MavenSettingListener;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.local.LocalMavenRepositorySettings;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Settings;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SimpleLocalRepositorySettings implements LocalMavenRepositorySettings {
    private final static Log log = LogFactory.getLog(SimpleLocalRepositorySettings.class);

    private volatile File repository;

    private volatile boolean downloadSourcesAutomatically;

    private volatile boolean downloadDocsAutomatically;

    private volatile boolean downloadAnnotationsAutomatically;

    public SimpleLocalRepositorySettings(AnActionEvent event) {
        // 如果idea中安装了 Maven 插件
        if (ClassUtils.forName("org.jetbrains.idea.maven.project.MavenProjectsManager") != null) {
            MavenSettingListener.start(event, this);
        }

        // 如果 idea 中没有安装 maven 插件
        if (this.repository == null) {
            this.findLocalRepository();
        }

        if (this.repository == null) {
            log.warn("No Maven local repository found!");
        }
    }

    protected void findLocalRepository() {
        File m2 = new File(Settings.getUserHome(), ".m2");
        if (m2.exists() && m2.isDirectory()) {
            File repository = new File(m2, "repository");
            if (repository.exists() && repository.isDirectory()) {
                this.repository = repository;
            }
        }
    }

    public File getRepository() {
        return this.repository;
    }

    public void setRepository(File dir) {
        this.repository = Ensure.notNull(dir);
    }

    public boolean isDownloadSourcesAutomatically() {
        return downloadSourcesAutomatically;
    }

    public void setDownloadSourcesAutomatically(boolean downloadSourcesAutomatically) {
        this.downloadSourcesAutomatically = downloadSourcesAutomatically;
    }

    public boolean isDownloadDocsAutomatically() {
        return downloadDocsAutomatically;
    }

    public void setDownloadDocsAutomatically(boolean downloadDocsAutomatically) {
        this.downloadDocsAutomatically = downloadDocsAutomatically;
    }

    public boolean isDownloadAnnotationsAutomatically() {
        return downloadAnnotationsAutomatically;
    }

    public void setDownloadAnnotationsAutomatically(boolean downloadAnnotationsAutomatically) {
        this.downloadAnnotationsAutomatically = downloadAnnotationsAutomatically;
    }
}
