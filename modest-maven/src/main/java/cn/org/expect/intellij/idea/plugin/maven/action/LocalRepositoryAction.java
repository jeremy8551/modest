package cn.org.expect.intellij.idea.plugin.maven.action;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Maven 本地仓库
 */
public abstract class LocalRepositoryAction extends AnAction {

    public LocalRepositoryAction(String title) {
        super(title);
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        MavenSearchPlugin plugin = new MavenSearchPlugin(event);
        File repository = plugin.getLocalRepositorySettings().getRepository();
        if (repository == null) {
            plugin.sendNotification(ArtifactSearchNotification.ERROR, "maven.search.error.cannot.found.local.repository");
            return;
        }

        this.execute(plugin, repository);
    }

    /**
     * 执行业务逻辑
     *
     * @param plugin     插件接口
     * @param repository 本地仓库目录
     */
    public abstract void execute(MavenSearchPlugin plugin, File repository);
}
