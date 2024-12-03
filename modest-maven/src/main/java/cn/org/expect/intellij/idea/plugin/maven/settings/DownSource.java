package cn.org.expect.intellij.idea.plugin.maven.settings;

public enum DownSource {

    CENTRAL("central"), MAVEN("maven"), ALIYUN("aliyun"), HUAWEI("huawei"), TENCENT("tencent");

    private final String name;

    DownSource(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
