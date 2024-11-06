package cn.org.expect.intellijidea.plugin.maven.action;

import java.io.File;

import cn.org.expect.intellijidea.plugin.maven.MavenFinder;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderContext;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * 打开 Maven 本地仓库
 */
public class MavenFinderOpenLocalMavenRepository extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MavenFinderContext context = new MavenFinderContext(e);
        MavenFinder mavenFinder = new MavenFinder(context);
        String filepath = mavenFinder.getLocalMavenRepository().getAddress();
        if (StringUtils.isBlank(filepath)) {
            mavenFinder.sendErrorNotification("Cannot find Maven local repository!");
            return;
        }

        BrowserUtil.browse(new File(filepath).getParentFile());
    }
}
