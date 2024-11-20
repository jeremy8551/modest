package cn.org.expect.intellij.idea.plugin.maven.action;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginFactory;
import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
import cn.org.expect.maven.search.MavenSearchNotification;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 打开 Maven 本地仓库
 */
public class OpenLocalRepository extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent event) {
        MavenSearchPluginFactory.createEasyContext(event);
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);
        File repository = plugin.getEasyContext().getBean(LocalRepositoryConfig.class).getRepository();
        if (repository == null) {
            plugin.sendNotification(MavenSearchNotification.ERROR, "Cannot find Maven local repository!");
            return;
        }

        plugin.sendNotification(MavenSearchNotification.NORMAL, repository.getAbsolutePath());
        BrowserUtil.browse(repository);
    }
}
