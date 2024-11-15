package cn.org.expect.maven.intellij.idea.listener;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.intellij.idea.log.IdeaLogBuilder;
import cn.org.expect.util.ClassUtils;
import com.intellij.ide.ApplicationInitializedListener;

/**
 * Idea 启动器：在 Idea 启动后执行的业务逻辑
 */
public class MavenSearchPluginApplicationListener implements ApplicationInitializedListener {

    public void componentsInitialized() {
        if (Boolean.parseBoolean(System.getProperty("idea.is.internal"))) { // 如果是开发模式
            DefaultEasyContext.newInstance(this.getClass().getClassLoader(), "sout+:info", ClassUtils.getPackageName(MavenSearchPluginApplicationListener.class, 4) + ":debug");
        } else {
            LogFactory.getContext().setBuilder(new IdeaLogBuilder());
            DefaultEasyContext.newInstance(this.getClass().getClassLoader(), "sout+:info");
        }
    }
}
