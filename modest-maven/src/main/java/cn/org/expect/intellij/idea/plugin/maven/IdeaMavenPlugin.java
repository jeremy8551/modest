package cn.org.expect.intellij.idea.plugin.maven;

import java.io.File;

import cn.org.expect.maven.repository.local.LocalRepositorySettings;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenImportingSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenWslUtil;

/**
 * Idea 官方 Maven插件的操作实现类
 */
public class IdeaMavenPlugin {

    public IdeaMavenPlugin() {
    }

    /**
     * 判断是否已安装 Maven 官方插件
     *
     * @return 返回true表示已安装 false表示未安装
     */
    public boolean isMavenPluginEnable() {
        return ClassUtils.forName("org.jetbrains.idea.maven.project.MavenProjectsManager") != null;
    }

    /**
     * 返回 Maven插件中配置的 settings.xml 文件
     *
     * @param event 事件
     * @return 文件
     */
    public VirtualFile getSettingsXml(AnActionEvent event) {
        MavenProjectsManager manager = MavenProjectsManager.getInstance(event.getProject());
        if (manager != null) {
            MavenGeneralSettings generalSettings = manager.getGeneralSettings();
            if (generalSettings != null) {
                File userSettings = MavenWslUtil.getUserSettings(event.getProject(), generalSettings.getUserSettingsFile(), generalSettings.getMavenConfig());  // settings.xml
                if (userSettings != null) {
                    return LocalFileSystem.getInstance().findFileByIoFile(userSettings);
                }
            }
        }
        return null;
    }

    /**
     * 加载 Maven 官方插件的配置
     *
     * @param settings 本地仓库配置信息
     * @param event    事件
     */
    public void load(LocalRepositorySettings settings, AnActionEvent event) {
        MavenProjectsManager manager = MavenProjectsManager.getInstance(Ensure.notNull(event).getProject());
        settings.setRepository(this.getRepository(manager));
        MavenImportingSettings importingSettings = manager.getImportingSettings();
        if (importingSettings != null) {
            settings.setDownloadSourcesAutomatically(importingSettings.isDownloadSourcesAutomatically());
            settings.setDownloadDocsAutomatically(importingSettings.isDownloadDocsAutomatically());
            settings.setDownloadAnnotationsAutomatically(importingSettings.isDownloadAnnotationsAutomatically());
        }
    }

    protected File getRepository(MavenProjectsManager manager) {
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
            return new File(filepath);
        } else {
            return this.findDefaultRepository();
        }
    }

    protected File findDefaultRepository() {
        File home = new File(Settings.getUserHome(), ".m2");
        if (home.exists() && home.isDirectory()) {
            File repository = new File(home, "repository");
            if (repository.exists() && repository.isDirectory()) {
                return repository;
            }
        }
        return null;
    }
}
