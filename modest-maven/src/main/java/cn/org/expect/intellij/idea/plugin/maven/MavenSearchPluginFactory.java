package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.intellij.idea.plugin.maven.action.MavenSearchPluginPinAction;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenPluginJob;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPluginFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        MavenSearchPluginPinAction.PIN.dispose();
        MavenSearchPluginContributor contributor = this.create(event);
        contributor.getPlugin().execute(new MavenPluginJob());
        return contributor;
    }

    public @NotNull MavenSearchPluginContributor create(@NotNull AnActionEvent event) {
        MavenSearchPlugin plugin = new MavenSearchPlugin(event);
        plugin.updateTabTooltip();
        return plugin.getContributor();
    }
}
