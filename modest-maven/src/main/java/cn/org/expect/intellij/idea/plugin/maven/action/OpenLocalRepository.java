package cn.org.expect.intellij.idea.plugin.maven.action;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.MavenMessage;
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
        super(MavenMessage.get("maven.search.open.local.repository.menu"));
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        MavenSearchPlugin plugin = new MavenSearchPlugin(event);
        File repository = plugin.getLocalRepositorySettings().getRepository();
        if (repository == null) {
            plugin.sendNotification(ArtifactSearchNotification.ERROR, "maven.search.error.cannot.found.local.repository");
            return;
        }

        plugin.sendNotification(ArtifactSearchNotification.NORMAL, repository.getAbsolutePath());
        BrowserUtil.browse(repository);
    }
}
