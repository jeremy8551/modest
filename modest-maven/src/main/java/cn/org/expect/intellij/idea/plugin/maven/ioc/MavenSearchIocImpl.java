package cn.org.expect.intellij.idea.plugin.maven.ioc;

import cn.org.expect.ioc.DefaultEasyContext;

public class MavenSearchIocImpl extends DefaultEasyContext implements MavenSearchIoc {

    public MavenSearchIocImpl(String... args) {
        super(args);
    }

    public MavenSearchIocImpl(ClassLoader classLoader, String... args) {
        super(classLoader, args);
    }
}
