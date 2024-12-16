package cn.org.expect.intellij.idea.plugin.maven.action;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * 打开 Maven 配置的 settings.xml
 */
public class OpenMavenSettingsXmlAction extends AnAction {

    public OpenMavenSettingsXmlAction() {
        super(MavenMessage.get("maven.search.open.local.settings.xml.menu"));
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        MavenSearchPlugin plugin = new MavenSearchPlugin(event);
        if (plugin.getIdeaMavenPlugin().isMavenPluginEnable()) {
            VirtualFile settingsXml = plugin.getIdeaMavenPlugin().getSettingsXml(event);
            if (settingsXml != null) {
                FileEditorManager.getInstance(event.getProject()).openFile(settingsXml, true); // 使用 IDE 的文件编辑器打开文件
            }
        } else {
            plugin.sendNotification(ArtifactSearchNotification.ERROR, "maven.search.error.cannot.setup.maven.plugin");
        }
    }
}
