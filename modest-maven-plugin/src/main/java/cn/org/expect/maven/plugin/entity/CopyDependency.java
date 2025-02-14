package cn.org.expect.maven.plugin.entity;

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

    public String toString() {
        return "DependencyModule{" + "copy='" + copy + '\'' + ", to='" + to + '\'' + '}';
    }
}
