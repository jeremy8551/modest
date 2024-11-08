package cn.org.expect.maven.intellij.idea;

import java.io.File;

import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Settings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

public class RepositoryConfigFactory implements LocalRepositoryConfig {
    private static final Logger log = Logger.getInstance(RepositoryConfigFactory.class);

    private static volatile File REPOSITORY;

    private static volatile RepositoryConfigFactory INSTANCE;

    public static RepositoryConfigFactory getInstance(AnActionEvent event) {
        if (INSTANCE == null) {
            synchronized (RepositoryConfigFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RepositoryConfigFactory(event);
                }
            }
        }
        return INSTANCE;
    }

    protected RepositoryConfigFactory(AnActionEvent event) {
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
                RepositoryConfigFactory.REPOSITORY = repository;
            }
        }
    }

    @Override
    public File getRepository() {
        return REPOSITORY;
    }

    public static void setRepository(File dir) {
        RepositoryConfigFactory.REPOSITORY = Ensure.notNull(dir);
    }
}
