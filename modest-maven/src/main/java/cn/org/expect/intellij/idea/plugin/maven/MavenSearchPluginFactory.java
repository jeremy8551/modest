package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.AbstractMavenSearch;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.util.ClassUtils;
import com.intellij.CommonBundle;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPluginFactory implements SearchEverywhereContributorFactory<Object> {
    private final static Log log = LogFactory.getLog(MavenSearchPluginFactory.class);

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        if (AbstractMavenSearch.getEasyContext() == null) {
            synchronized (AbstractMavenSearch.class) {
                if (AbstractMavenSearch.getEasyContext() == null) {
                    this.init();
                }
            }
        }

        MavenSearchMessage.setChineseCondition((key) -> "取消".equals(CommonBundle.getCancelButtonText())); // TODO 位置需要改
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);
        plugin.updateTabTooltip();
        plugin.execute(new MavenSearchPluginJob());
        return plugin.getContributor();
    }

    public void init() {
        EasyContext ioc = new DefaultEasyContext(this.getClass().getClassLoader(), "sout+:info", Boolean.parseBoolean(System.getProperty("idea.is.internal")) ? ClassUtils.getPackageName(MavenSearchPluginApplication.class, 4) + ":debug" : "");
        AbstractMavenSearch.setEasyContext(ioc);

        String packageName = MavenSearchPluginApplication.class.getPackage().getName();
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        for (IdeaPluginDescriptor pluginDescriptor : plugins) {
            String id = pluginDescriptor.getPluginId().getIdString();
            if (id.equals(packageName)) {
                AbstractMavenSearch.setId(id);
                String name = pluginDescriptor.getName();
                AbstractMavenSearch.setName(name);

                if (log.isDebugEnabled()) {
                    log.debug("plugin ID: {}, name: {}", id, name);
                }
            }
        }
    }
}
