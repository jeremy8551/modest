package cn.org.expect.maven.plugin.copyModule;

import java.util.List;

/**
 * 描述 Maven 多模块工程中的模块复制配置
 */
public class Module {

    private String name;

    private List<Path> paths;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(final List<Path> paths) {
        this.paths = paths;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "CopySourceModule{" + "name='" + name + '\'' + ", paths=" + paths + '}';
    }
}
