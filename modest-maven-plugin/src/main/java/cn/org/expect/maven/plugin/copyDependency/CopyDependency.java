package cn.org.expect.maven.plugin.copyDependency;

/**
 * 描述需要复制的 Maven 依赖及其目标位置
 */
public class CopyDependency {

    private String copy;

    private String to;

    public String getCopy() {
        return copy;
    }

    public void setCopy(String copy) {
        this.copy = copy;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "DependencyModule{" + "copy='" + copy + '\'' + ", to='" + to + '\'' + '}';
    }
}
