package cn.org.expect.intellij.idea.plugin.maven;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.log.IdeaLogBuilder;
import cn.org.expect.intellij.idea.plugin.maven.settings.MavenPluginSettings;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.MavenEasyContext;
import cn.org.expect.maven.impl.SimpleMavenEasyContext;
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
    private final static Log log = LogFactory.getLog(MavenSearchPluginApplication.class);

    private static volatile MavenEasyContext instance;

    /** 锁 */
    protected final static Object lock = new Object();

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
    public static MavenEasyContext get() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = create();
                }
            }
        }
        return instance;
    }

    /**
     * 创建容器上下文信息
     *
     * @return 容器上下文信息
     */
    private static MavenEasyContext create() {
        boolean debug = Boolean.parseBoolean(System.getProperty("idea.is.internal"));
        if (!debug) {
            LogFactory.getContext().setBuilder(new IdeaLogBuilder());
        }

        // 容器接口
        MavenEasyContext ioc = new SimpleMavenEasyContext(MavenSearchPluginFactory.class.getClassLoader(), //
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
                MavenPluginSettings settings = ioc.getBean(MavenPluginSettings.class);
                settings.setId(id); // 插件ID
                settings.setName(descriptor.getName()); // 插件名
                settings.load();
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
