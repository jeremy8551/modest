package cn.org.expect.maven.repository.local;

import java.io.File;

import cn.org.expect.maven.intellij.idea.MavenSettingListener;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Settings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

public class LocalRepositoryConfig {
    private static final Logger log = Logger.getInstance(LocalRepositoryConfig.class);

    private static volatile File REPOSITORY;

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

    protected LocalRepositoryConfig(AnActionEvent event) {
        // 如果idea中安装了 Maven 插件
        if (ClassUtils.forName("org.jetbrains.idea.maven.project.MavenProjectsManager") != null) {
            MavenSettingListener.start(event);
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

    public static void setRepository(File dir) {
        LocalRepositoryConfig.REPOSITORY = Ensure.notNull(dir);
    }
}
