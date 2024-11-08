package cn.org.expect.maven.intellij.idea;

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
    public static void start(AnActionEvent event) {
        if (INSTANCE == null) {
            synchronized (MavenSettingListener.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MavenSettingListener(event);
                }
            }
        }
    }

    private final AnActionEvent event;

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

    @Override
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
                LocalRepositoryConfig.setRepository(new File(filepath)); // 获取 Maven 本地仓库路径
            }

            return manager;
        }
        return null;
    }

    @Override
    public void dispose() {
    }
}
