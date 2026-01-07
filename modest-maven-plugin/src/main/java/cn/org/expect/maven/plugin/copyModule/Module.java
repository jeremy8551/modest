package cn.org.expect.maven.plugin.copyModule;

import java.util.List;

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

    public String toString() {
        return "CopySourceModule{" + "name='" + name + '\'' + ", paths=" + paths + '}';
    }
}
