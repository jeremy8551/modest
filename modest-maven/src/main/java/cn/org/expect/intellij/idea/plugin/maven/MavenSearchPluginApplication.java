package cn.org.expect.intellij.idea.plugin.maven;

import java.util.List;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.AbstractMavenSearch;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import org.jetbrains.annotations.NotNull;

/**
 * 插件启动器：在 Idea 启动后执行的业务逻辑
 */
public class MavenSearchPluginApplication implements AppLifecycleListener {
    private final static Log log = LogFactory.getLog(MavenSearchPluginApplication.class);

    public MavenSearchPluginApplication() {
    }

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        if (log.isDebugEnabled()) {
            log.debug("appFrameCreated({}) ", StringUtils.toString(commandLineArgs));
        }
    }

    @Override
    public void welcomeScreenDisplayed() {
        if (log.isDebugEnabled()) {
            log.debug("welcomeScreenDisplayed() ");
        }
    }

    @Override
    public void appStarted() {
        if (log.isDebugEnabled()) {
            log.debug("appStarted() ");
        }
        this.init();
    }

    @Override
    public void projectFrameClosed() {
        if (log.isDebugEnabled()) {
            log.debug("projectFrameClosed() ");
        }
    }

    @Override
    public void projectOpenFailed() {
        if (log.isDebugEnabled()) {
            log.debug("projectOpenFailed() ");
        }
    }

    @Override
    public void appClosing() {
        if (log.isDebugEnabled()) {
            log.debug("appClosing() ");
        }
    }

    @Override
    public void appWillBeClosed(boolean isRestart) {
        if (log.isDebugEnabled()) {
            log.debug("appWillBeClosed() ");
        }
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
