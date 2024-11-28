package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.awt.*;

import cn.org.expect.intellij.idea.plugin.maven.IdeaSearchUI;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.action.MavenSearchPluginPinAction;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MavenSearchPluginPinJob extends MavenSearchPluginInitJob implements EDTJob {

    /** 生成 pin 窗口的原生窗口使用的 MavenSearchPlugin */
    private final MavenSearchPlugin oldPlugin;

    private final SearchEverywhereUI oldUI;

    private final Runnable actionPerformed;

    public MavenSearchPluginPinJob(MavenSearchPlugin plugin, Runnable actionPerformed) {
        super();
        this.oldPlugin = plugin;
        this.oldUI = plugin.getIdeaUI().getSearchEverywhereUI();
        this.actionPerformed = actionPerformed;
    }

    @Override
    public SearchEverywhereUI getSearchEverywhereUI(AnActionEvent event) {
        return MavenSearchPluginPinAction.PIN.getUI();
    }

    @Override
    public int execute() {
        super.execute();

        String pattern = this.oldPlugin.getIdeaUI().getSearchEverywhereUI().getSearchField().getText();
        IdeaSearchUI.StatusBar statusBar = this.oldPlugin.getIdeaUI().getStatusBar();
        String repositoryId = this.oldPlugin.getRepositoryId();
        int size = this.oldPlugin.getIdeaUI().getDisplay().size();
        Rectangle visibleRect = this.oldPlugin.getIdeaUI().getDisplay().getVisibleRect();

        // 设置搜索接口
        MavenSearchPlugin plugin = this.getSearch();
        plugin.setRepositoryId(repositoryId);
        plugin.getContext().setVisibleRect(visibleRect);

        // 复制搜索文本
        if (StringUtils.isNotBlank(pattern)) {
            plugin.getIdeaUI().getSearchEverywhereUI().getSearchField().setText(pattern);
        }

        // 复制状态栏
        if (statusBar != null) {
            plugin.getIdeaUI().setStatusBar(statusBar.getType(), statusBar.getMessage());
        }

        plugin.display();
        MavenSearchPluginPinAction.PIN.show(this.oldUI.getSize(), this.oldUI.getLocationOnScreen(), StringUtils.isBlank(pattern) && size == 0);
        this.actionPerformed.run(); // 设置pin按钮按下
        this.oldUI.dispose(); // 销毁
        return 0;
    }
}
