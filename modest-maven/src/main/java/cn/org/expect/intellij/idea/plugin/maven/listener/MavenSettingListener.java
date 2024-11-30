package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.io.File;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.local.LocalMavenRepositorySettings;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenImportingSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

/**
 * Idea 的 Maven 插件监听器，如果 Settings 中 Maven 本地仓库位置发生变化时进行通知
 */
public class MavenSettingListener implements MavenGeneralSettings.Listener, Disposable {
    private final static Log log = LogFactory.getLog(MavenSettingListener.class);

    private static volatile MavenSettingListener LISTENER;

    /**
     * 开始监听 Maven 插件
     *
     * @param event 事件
     */
    public static void start(AnActionEvent event, LocalMavenRepositorySettings config) {
        if (LISTENER == null) {
            synchronized (MavenSettingListener.class) {
                if (LISTENER == null) {
                    LISTENER = new MavenSettingListener(event, config);
                }
            }
        }

        LISTENER.setEvent(event);
        LISTENER.setConfig(config);
    }

    private volatile AnActionEvent event;

    private volatile LocalMavenRepositorySettings config;

    protected MavenSettingListener(AnActionEvent event, LocalMavenRepositorySettings config) {
        this.event = Ensure.notNull(event);
        this.config = Ensure.notNull(config);

        MavenProjectsManager manager = this.execute(this.event);
        if (manager != null) {
            MavenGeneralSettings settings = manager.getGeneralSettings();
            if (settings != null) {
                settings.addListener(this, this);
            }
        }
    }

    public void setEvent(AnActionEvent event) {
        this.event = event;
    }

    public void setConfig(LocalMavenRepositorySettings config) {
        this.config = config;
    }

    public void changed() {
        if (log.isDebugEnabled()) {
            log.debug("{}.changed(). ", this.getClass().getSimpleName());
        } // TODO 找到不输出日志的原因
        this.execute(this.event);
    }

    protected MavenProjectsManager execute(AnActionEvent event) {
        Project project = Ensure.notNull(event.getProject());
        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
        if (manager != null) {
            String filepath = null;

            MavenGeneralSettings settings = manager.getGeneralSettings();
            if (settings != null) {
                filepath = settings.getLocalRepository();
            }

            if (StringUtils.isBlank(filepath)) {
                File file = manager.getLocalRepository();
                if (file != null) {
                    filepath = file.getAbsolutePath();
                }
            }

            if (StringUtils.isNotBlank(filepath)) {
                this.config.setRepository(new File(filepath)); // 获取 Maven 本地仓库路径
            }

            MavenImportingSettings importingSettings = manager.getImportingSettings();
            this.config.setDownloadSourcesAutomatically(importingSettings.isDownloadSourcesAutomatically());
            this.config.setDownloadDocsAutomatically(importingSettings.isDownloadDocsAutomatically());
            this.config.setDownloadAnnotationsAutomatically(importingSettings.isDownloadAnnotationsAutomatically());
            return manager;
        }
        return null;
    }

    public void dispose() {
    }
}
