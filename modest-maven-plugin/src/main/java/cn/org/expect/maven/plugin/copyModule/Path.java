package cn.org.expect.maven.plugin.copyModule;

/**
 * 描述模块文件的来源路径与目标路径
 */
public class Path {

    private String src;

    public String getSrc() {
        return src;
    }

    public void setSrc(final String src) {
        this.src = src;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "CopyPath{" + "copy='" + src + '\'' + '}';
    }
}
