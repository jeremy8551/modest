package cn.org.expect.intellij.idea.plugin.maven.action;

import java.io.File;
import java.util.Objects;

import cn.org.expect.intellij.idea.plugin.maven.IdeaMavenUtils;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginFactory;
import cn.org.expect.maven.search.ArtifactSearchMessage;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenWslUtil;

/**
 * 打开 Maven 配置的 settings.xml
 */
public class OpenMavenSettingsXml extends AnAction {

    public OpenMavenSettingsXml() {
        super(ArtifactSearchMessage.get("maven.search.open.local.settings.xml.menu"));
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        if (IdeaMavenUtils.hasSetupMavenPlugin()) {
            Project project = Objects.requireNonNull(event.getProject());
            MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
            if (manager != null) {
                MavenGeneralSettings settings = manager.getGeneralSettings();
                if (settings != null) {
                    // settings.xml
                    File userSettings = MavenWslUtil.getUserSettings(event.getProject(), settings.getUserSettingsFile(), settings.getMavenConfig());
                    if (userSettings != null) {
                        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(userSettings);
                        if (virtualFile != null) {
                            FileEditorManager.getInstance(project).openFile(virtualFile, true); // 使用 IDE 的文件编辑器打开文件
                        }
                    }
                }
            }
        } else {
            MavenSearchPluginFactory.loadLocalRepositoryConfig(event);
            MavenSearchPluginContext context = new MavenSearchPluginContext(event);
            MavenSearchPlugin plugin = new MavenSearchPlugin(context);
            plugin.sendNotification(ArtifactSearchNotification.ERROR, ArtifactSearchMessage.get("maven.search.error.cannot.setup.maven.plugin"));
        }
    }
}
