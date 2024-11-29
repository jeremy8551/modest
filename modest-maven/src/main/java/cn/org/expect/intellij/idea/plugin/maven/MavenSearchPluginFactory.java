package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.intellij.idea.plugin.maven.action.MavenSearchPluginPinAction;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchPluginInitJob;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.impl.EasyBeanDefineImpl;
import cn.org.expect.maven.repository.local.LocalRepositorySettings;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPluginFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        MavenSearchPluginPinAction.PIN.dispose();
        MavenSearchPluginFactory.loadLocalRepositoryConfig(event);
        MavenSearchPluginContributor contributor = this.create(event);
        contributor.getPlugin().execute(new MavenSearchPluginInitJob());
        return contributor;
    }

    public @NotNull MavenSearchPluginContributor create(@NotNull AnActionEvent event) {
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);
        plugin.updateTabTooltip();
        return plugin.getContributor();
    }

    /**
     * 注册 Maven 本地仓库配置信息
     *
     * @param event 事件信息
     */
    public static void loadLocalRepositoryConfig(AnActionEvent event) {
        EasyContext ioc = MavenSearchPluginApplication.get();
        if (!ioc.containsBeanInfo(LocalRepositorySettings.class, SimpleLocalRepositorySettings.class)) {
            EasyBeanDefineImpl bean = new EasyBeanDefineImpl(SimpleLocalRepositorySettings.class);
            bean.setBean(new SimpleLocalRepositorySettings(event));
            bean.setSingleton(true);
            ioc.addBean(bean);
        }
    }
}
