package cn.org.expect.maven.plugin;

/**
 * 让其他组件感知到：日志输出统一使用Maven插件日志
 */
public interface MavenPluginLogAware {

    /**
     * 使用日志
     *
     * @param log Maven插件日志
     */
    void use(MavenPluginLog log);
}
