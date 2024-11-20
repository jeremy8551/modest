package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.io.File;

import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

/**
 * Idea 的 Maven 插件监听器，如果 Settings 中 Maven 本地仓库位置发生变化时进行通知
 */
public class MavenSettingListener implements MavenGeneralSettings.Listener, Disposable {

    private static volatile MavenSettingListener INSTANCE;

    /**
     * 开始监听 Maven 插件
     *
     * @param event 事件
     */
    public static void start(AnActionEvent event, LocalRepositoryConfig config) {
        if (INSTANCE == null) {
            synchronized (MavenSettingListener.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MavenSettingListener(event);
                }
            }
        }

        INSTANCE.setEvent(event);
        INSTANCE.setConfig(config);
    }

    private volatile AnActionEvent event;

    private volatile LocalRepositoryConfig config;

    protected MavenSettingListener(AnActionEvent event) {
        this.event = Ensure.notNull(event);
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

    public void setConfig(LocalRepositoryConfig config) {
        this.config = config;
    }

    public void changed() {
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

            return manager;
        }
        return null;
    }

    public void dispose() {
    }
}
