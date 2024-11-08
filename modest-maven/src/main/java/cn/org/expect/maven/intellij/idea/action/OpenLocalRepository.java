package cn.org.expect.maven.intellij.idea.action;

import java.io.File;

import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.search.MavenContext;
import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
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
        MavenContext context = new MavenContext(event);
        MavenSearch mavenFinder = new MavenSearch(context);
        File repository = LocalRepositoryConfig.getInstance(event).getRepository();
        if (repository == null) {
            mavenFinder.sendErrorNotification("Cannot find Maven local repository!");
            return;
        }

        mavenFinder.sendNotification(repository.getAbsolutePath());
        BrowserUtil.browse(repository);
    }
}
