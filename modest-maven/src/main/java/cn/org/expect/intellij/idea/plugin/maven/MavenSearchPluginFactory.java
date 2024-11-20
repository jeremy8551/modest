package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.impl.EasyBeanDefineImpl;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.MavenSearch;
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

    private static volatile EasyContext INSTANCE;

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        MavenSearchPluginFactory.createEasyContext(event);
        MavenSearchMessage.setChineseCondition((key) -> "取消".equals(CommonBundle.getCancelButtonText())); // TODO 位置需要改
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);
        plugin.updateTabTooltip();
        plugin.execute(new MavenSearchPluginJob());
        return plugin.getContributor();
    }

    public static void createEasyContext(AnActionEvent event) {
        if (INSTANCE == null) {
            synchronized (MavenSearchPluginFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = create(event);
                }
            }
        }
    }

    private static EasyContext create(AnActionEvent event) {
        boolean debug = Boolean.parseBoolean(System.getProperty("idea.is.internal"));
        EasyContext ioc = DefaultEasyContext.newInstance(MavenSearchPluginFactory.class.getClassLoader(), //
                "sout+:info", // 默认日志级别
                debug ? ClassUtils.getPackageName(MavenSearchPluginApplication.class, 4) + ":debug" : "", //
                debug ? ClassUtils.getPackageName(MavenSearch.class, 4) + ":debug" : "" //
        );

        // 注册 Maven 本地仓库的注册信息
        EasyBeanDefineImpl bean1 = new EasyBeanDefineImpl(SimpleLocalRepositoryConfig.class);
        bean1.setBean(new SimpleLocalRepositoryConfig(event));
        bean1.setSingleton(true);
        ioc.addBean(bean1);

        // 设置插件ID与插件名
        String packageName = MavenSearchPluginApplication.class.getPackage().getName();
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        for (IdeaPluginDescriptor descriptor : plugins) {
            String id = descriptor.getPluginId().getIdString();
            if (id.equals(packageName)) {
                String name = descriptor.getName(); // 插件名

                // 注册插件信息
                EasyBeanDefineImpl bean2 = new EasyBeanDefineImpl(MavenSearchPluginConfig.class);
                bean2.setBean(new MavenSearchPluginConfig(id, name));
                bean2.setSingleton(true);
                ioc.addBean(bean2);

                if (log.isDebugEnabled()) {
                    log.debug("plugin ID: {}, name: {}", id, name);
                }
            }
        }

        return ioc;
    }
}
