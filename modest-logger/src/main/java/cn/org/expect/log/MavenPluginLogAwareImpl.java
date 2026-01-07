package cn.org.expect.log;

import cn.org.expect.maven.plugin.MavenPluginLog;
import cn.org.expect.maven.plugin.MavenPluginLogAware;
import com.google.auto.service.AutoService;

/**
 * 基于 SPI 机制加载 Maven 插件日志
 */
@AutoService(MavenPluginLogAware.class)
public class MavenPluginLogAwareImpl implements MavenPluginLogAware {

    public void use(MavenPluginLog log) {
        LogFactory.getContext().setBuilder(new SingletonLogBuilder(log));
    }
}
