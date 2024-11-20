package cn.org.expect.intellij.idea.plugin.maven;

/**
 * 插件配置信息
 */
public class MavenSearchPluginConfig {

    /** 插件ID */
    private String id;

    /** 插件名 */
    private String name;

    public MavenSearchPluginConfig(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
