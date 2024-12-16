package cn.org.expect.intellij.idea.plugin.maven.action;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import com.intellij.ide.BrowserUtil;

/**
 * 打开 Maven 本地仓库
 */
public class OpenLocalRepositoryAction extends LocalRepositoryAction {

    public OpenLocalRepositoryAction() {
        super(MavenMessage.get("maven.search.open.local.repository.menu"));
    }

    public void execute(MavenSearchPlugin plugin, File repository) {
        plugin.sendNotification(ArtifactSearchNotification.NORMAL, repository.getAbsolutePath());
        BrowserUtil.browse(repository);
    }
}
