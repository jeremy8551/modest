package cn.org.expect.maven.intellij.idea;

import java.io.File;
import java.io.IOException;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.db.DatabaseSerializer;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
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
    private final static Log log = LogFactory.getLog(MavenSettingListener.class);

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
        File oldDir = DatabaseSerializer.getStoreDir(RepositoryConfigFactory.getInstance(this.event).getRepository());
        this.execute(this.event);
        File newDir = DatabaseSerializer.getStoreDir(RepositoryConfigFactory.getInstance(this.event).getRepository());

        if (log.isDebugEnabled()) {
            log.debug("move cache files from {} to {}", oldDir, newDir);
        }

        // 本地仓库路径变更后，需要迁移缓存文件
        if (newDir != null && oldDir != null && !newDir.equals(oldDir)) {
            File file1 = new File(oldDir, DatabaseSerializer.PATTERN_TABLE);
            File file2 = new File(oldDir, DatabaseSerializer.ARTIFACT_TABLE);
            try {
                if (this.move(file1, newDir) && this.move(file2, newDir)) {
                    FileUtils.deleteDirectory(oldDir);
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private boolean move(File file, File newDir) throws IOException {
        if (file.exists()) {
            File newfile = new File(newDir, file.getName());
            if (newfile.exists()) {
                if (FileUtils.copy(file, newfile) && file.delete()) {
                    return true;
                }
            } else {
                if (file.renameTo(newfile)) {
                    return true;
                }
            }
        }

        return !file.exists();
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
                RepositoryConfigFactory.setRepository(new File(filepath)); // 获取 Maven 本地仓库路径
            }

            return manager;
        }
        return null;
    }

    @Override
    public void dispose() {
    }
}
