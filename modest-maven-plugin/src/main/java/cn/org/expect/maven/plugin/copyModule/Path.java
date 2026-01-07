package cn.org.expect.maven.plugin.copyModule;

public class Path {

    private String src;

    public String getSrc() {
        return src;
    }

    public void setSrc(final String src) {
        this.src = src;
    }

    public String toString() {
        return "CopyPath{" + "copy='" + src + '\'' + '}';
    }
}
