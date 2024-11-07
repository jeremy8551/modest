package cn.org.expect.intellijidea.plugin.maven.local;

import java.io.File;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.Settings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public class LocalRepositoryConfig implements MavenGeneralSettings.Listener, Disposable {
    private static final Logger log = Logger.getInstance(LocalRepositoryConfig.class);

    private static volatile LocalRepositoryConfig INSTANCE;

    public static LocalRepositoryConfig getInstance(AnActionEvent event) {
        if (INSTANCE == null) {
            synchronized (LocalRepositoryConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalRepositoryConfig(event);
                }
            }
        }
        return INSTANCE;
    }

    private File repository;

    private final AnActionEvent event;

    protected LocalRepositoryConfig(AnActionEvent event) {
        this.event = Ensure.notNull(event);

        if (this.loadIdeaSettings(this.event)) {
            return;
        }

        // 如果 idea 中没有安装 maven 插件
        if (this.autoFind()) {
            return;
        }

        log.warn("No Maven local repository found!");
    }

    protected boolean autoFind() {
        File m2 = new File(Settings.getUserHome(), ".m2");
        if (m2.exists() && m2.isDirectory()) {
            File repository = new File(m2, "repository");
            if (repository.exists() && repository.isDirectory()) {
                this.repository = repository;
                return true;
            }
        }
        return false;
    }

    protected boolean loadIdeaSettings(AnActionEvent event) {
        Project project = event.getProject();
        MavenProjectsManager manager = MavenProjectsManager.getInstance(Ensure.notNull(project));
        if (manager != null) {
            String filepath = manager.getLocalRepository().getAbsolutePath();
//            System.out.println("filepath: " + filepath);

            MavenGeneralSettings settings = manager.getGeneralSettings();
            settings.addListener(this, this);

//            System.out.println(settings.getMavenHome());
//            System.out.println("Maven Local Repository Path: " + settings.getLocalRepository());

            this.repository = new File(filepath); // 获取 Maven 本地仓库路径
            return true;
        }
        return false;
    }

    public File getRepository() {
        return this.repository;
    }

    @Override
    public void changed() {
        this.loadIdeaSettings(this.event);
    }

    @Override
    public void dispose() {
    }
}
