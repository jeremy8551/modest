package cn.org.expect.maven.intellij.idea.action;

import java.io.File;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.intellij.idea.MavenSearchPlugin;
import cn.org.expect.maven.intellij.idea.MavenSearchPluginContext;
import cn.org.expect.maven.intellij.idea.RepositoryConfigFactory;
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

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        EasyContext ioc = DefaultEasyContext.getInstance();
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearch plugin = new MavenSearchPlugin(ioc, context);
        File repository = RepositoryConfigFactory.getInstance(event).getRepository();
        if (repository == null) {
            plugin.sendNotification(MavenSearchNotification.ERROR, "Cannot find Maven local repository!");
            return;
        }

        plugin.sendNotification(MavenSearchNotification.NORMAL, repository.getAbsolutePath());
        BrowserUtil.browse(repository);
    }
}
