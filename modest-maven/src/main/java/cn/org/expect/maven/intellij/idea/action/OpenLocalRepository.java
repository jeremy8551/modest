package cn.org.expect.maven.intellij.idea.action;

import java.io.File;

import cn.org.expect.maven.intellij.idea.MavenPlugin;
import cn.org.expect.maven.intellij.idea.MavenPluginContext;
import cn.org.expect.maven.search.SearchOperation;
import cn.org.expect.maven.intellij.idea.RepositoryConfigFactory;
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
        MavenPluginContext context = new MavenPluginContext(event);
        SearchOperation mavenFinder = new MavenPlugin(context);
        File repository = RepositoryConfigFactory.getInstance(event).getRepository();
        if (repository == null) {
            mavenFinder.sendErrorNotification("Cannot find Maven local repository!");
            return;
        }

        mavenFinder.sendNotification(repository.getAbsolutePath());
        BrowserUtil.browse(repository);
    }
}
