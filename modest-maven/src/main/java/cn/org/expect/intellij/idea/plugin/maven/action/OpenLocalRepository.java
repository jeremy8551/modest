package cn.org.expect.intellij.idea.plugin.maven.action;

import java.io.File;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.intellij.idea.plugin.maven.DefaultLocalRepositoryConfig;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.maven.search.MavenSearch;
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
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearch plugin = new MavenSearchPlugin(context);
        File repository = DefaultLocalRepositoryConfig.getInstance(event).getRepository();
        if (repository == null) {
            plugin.sendNotification(MavenSearchNotification.ERROR, "Cannot find Maven local repository!");
            return;
        }

        plugin.sendNotification(MavenSearchNotification.NORMAL, repository.getAbsolutePath());
        BrowserUtil.browse(repository);
    }
}
