package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.impl.EasyBeanDefineImpl;
import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPluginFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        MavenSearchPluginFactory.loadLocalRepositoryConfig(event);
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);
        plugin.updateTabTooltip();
        plugin.execute(new MavenSearchPluginJob());
        return plugin.getContributor();
    }

    /**
     * 注册 Maven 本地仓库配置信息
     *
     * @param event 事件信息
     */
    public static void loadLocalRepositoryConfig(AnActionEvent event) {
        EasyContext ioc = MavenSearchPluginApplication.get();
        if (!ioc.containsBeanInfo(LocalRepositoryConfig.class, SimpleLocalRepositoryConfig.class)) {
            EasyBeanDefineImpl bean = new EasyBeanDefineImpl(SimpleLocalRepositoryConfig.class);
            bean.setBean(new SimpleLocalRepositoryConfig(event));
            bean.setSingleton(true);
            ioc.addBean(bean);
        }
    }
}
