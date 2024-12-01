package cn.org.expect.intellij.idea.plugin.maven.action;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginFactory;
import cn.org.expect.maven.repository.local.LocalMavenRepositorySettings;
import cn.org.expect.maven.search.ArtifactSearchMessage;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 打开 Maven 本地仓库
 */
public class OpenLocalRepository extends AnAction {

    public OpenLocalRepository() {
        super(ArtifactSearchMessage.get("maven.search.open.local.repository.menu"));
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        MavenSearchPluginFactory.loadLocalRepositoryConfig(event);
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);
        File repository = plugin.getEasyContext().getBean(LocalMavenRepositorySettings.class).getRepository();
        if (repository == null) {
            plugin.sendNotification(ArtifactSearchNotification.ERROR, ArtifactSearchMessage.get("maven.search.error.cannot.found.local.repository"));
            return;
        }

        plugin.sendNotification(ArtifactSearchNotification.NORMAL, repository.getAbsolutePath());
        BrowserUtil.browse(repository);
    }
}
