package cn.org.expect.intellij.idea.plugin.maven;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.log.IdeaLogBuilder;
import cn.org.expect.intellij.idea.plugin.maven.settings.MavenSearchPluginSettingsImpl;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.impl.EasyBeanDefineImpl;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.ArtifactSearch;
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
    private final Log log = LogFactory.getLog(MavenSearchPluginApplication.class);

    private static volatile EasyContext INSTANCE;

    // Idea 启动后加载容器
    static {
        MavenSearchPluginApplication.get();
    }

    public MavenSearchPluginApplication() {
        if (log.isDebugEnabled()) {
            log.debug("new {}", this.getClass().getSimpleName());
        }
    }

    /**
     * 返回容器上下文信息
     *
     * @return 容器上下文信息
     */
    public static EasyContext get() {
        if (INSTANCE == null) {
            synchronized (MavenSearchPluginApplication.class) {
                if (INSTANCE == null) {
                    INSTANCE = create();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 创建容器上下文信息
     *
     * @return 容器上下文信息
     */
    private static EasyContext create() {
        boolean debug = Boolean.parseBoolean(System.getProperty("idea.is.internal"));
        if (!debug) {
            LogFactory.getContext().setBuilder(new IdeaLogBuilder());
        }

        EasyContext ioc = DefaultEasyContext.newInstance(MavenSearchPluginFactory.class.getClassLoader(), //
                debug ? "sout+:info" : "", // 默认日志级别
                debug ? ClassUtils.getPackageName(MavenSearchPluginApplication.class, 4) + ":debug" : "", //
                debug ? ClassUtils.getPackageName(ArtifactSearch.class, 4) + ":debug" : "" //
        );

        // 设置插件ID与插件名
        String packageName = MavenSearchPluginApplication.class.getPackage().getName();
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        for (IdeaPluginDescriptor descriptor : plugins) {
            String id = descriptor.getPluginId().getIdString();
            if (id.equals(packageName)) {
                String name = descriptor.getName(); // 插件名

                MavenSearchPluginSettingsImpl settings = new MavenSearchPluginSettingsImpl();
                settings.setId(id);
                settings.setName(name);
                settings.load();

                // 注册插件配置信息
                EasyBeanDefineImpl bean = new EasyBeanDefineImpl(MavenSearchPluginSettings.class);
                bean.setBean(settings);
                bean.setSingleton(true);
                ioc.addBean(bean);
            }
        }

        return ioc;
    }

    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        if (log.isDebugEnabled()) {
            log.debug("appFrameCreated({}) ", StringUtils.toString(commandLineArgs));
        }
    }

    public void welcomeScreenDisplayed() {
        if (log.isDebugEnabled()) {
            log.debug("welcomeScreenDisplayed() ");
        }
    }

    public void appStarted() {
        if (log.isDebugEnabled()) {
            log.debug("appStarted() ");
        }
    }

    public void projectFrameClosed() {
        if (log.isDebugEnabled()) {
            log.debug("projectFrameClosed() ");
        }
    }

    public void projectOpenFailed() {
        if (log.isDebugEnabled()) {
            log.debug("projectOpenFailed() ");
        }
    }

    public void appClosing() {
        if (log.isDebugEnabled()) {
            log.debug("appClosing() ");
        }
    }

    public void appWillBeClosed(boolean isRestart) {
        if (log.isDebugEnabled()) {
            log.debug("appWillBeClosed() ");
        }
    }
}
