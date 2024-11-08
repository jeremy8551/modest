package cn.org.expect.intellijidea.plugin.maven.local;

import java.io.File;

import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Settings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

public class LocalRepositoryConfig {
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

    protected static volatile File REPOSITORY;

    protected LocalRepositoryConfig(AnActionEvent event) {
        // 如果idea中安装了 Maven 插件
        if (ClassUtils.forName("org.jetbrains.idea.maven.project.MavenProjectsManager") != null) {
            IdeaMavenPluginListener.start(event);
        }

        // 如果 idea 中没有安装 maven 插件
        if (REPOSITORY == null) {
            this.findLocalRepository();
        }

        if (REPOSITORY == null) {
            log.warn("No Maven local repository found!");
        }
    }

    protected void findLocalRepository() {
        File m2 = new File(Settings.getUserHome(), ".m2");
        if (m2.exists() && m2.isDirectory()) {
            File repository = new File(m2, "repository");
            if (repository.exists() && repository.isDirectory()) {
                LocalRepositoryConfig.REPOSITORY = repository;
            }
        }
    }

    public File getRepository() {
        return REPOSITORY;
    }
}
