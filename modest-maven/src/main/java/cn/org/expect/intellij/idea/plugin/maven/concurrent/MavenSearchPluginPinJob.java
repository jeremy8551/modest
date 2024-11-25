package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.intellij.idea.plugin.maven.IdeaSearchUI;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.action.MavenSearchPluginPinAction;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MavenSearchPluginPinJob extends MavenSearchPluginJob {

    /** 生成 pin 窗口的原生窗口使用的 MavenSearchPlugin */
    private final MavenSearchPlugin oldPlugin;

    private final SearchEverywhereUI oldUI;

    public MavenSearchPluginPinJob(MavenSearchPlugin plugin) {
        this.oldPlugin = plugin;
        this.oldUI = plugin.getIdeaUI().getSearchEverywhereUI();
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

        MavenSearchPlugin plugin = (MavenSearchPlugin) this.getSearch();
        plugin.setRepositoryId(repositoryId);
        plugin.getIdeaUI().getSearchEverywhereUI().getSearchField().setText(pattern); // 复制搜索文本
        plugin.getIdeaUI().setStatusBar(statusBar.getType(), statusBar.getMessage()); // 复制状态栏
        QUEUE.add(plugin::showSearchResult);

        // 显示UI界面
        MavenSearchPluginPinAction.PIN.show(this.oldUI.getSize(), this.oldUI.getLocationOnScreen());
        this.oldUI.dispose(); // 销毁
        return 0;
    }
}
