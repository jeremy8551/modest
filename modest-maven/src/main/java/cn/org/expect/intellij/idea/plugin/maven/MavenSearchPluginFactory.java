package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.search.MavenSearchMessage;
import com.intellij.CommonBundle;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPluginFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        MavenSearchMessage.setChineseCondition((key) -> "取消".equals(CommonBundle.getCancelButtonText())); // TODO 位置需要改
        EasyContext ioc = DefaultEasyContext.getInstance();
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(ioc, context);
        plugin.updateTabTooltip();
        plugin.execute(new MavenSearchPluginJob());
        return plugin.getContributor();
    }
}
