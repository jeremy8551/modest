package cn.org.expect.maven.ioc;

import cn.org.expect.ioc.DefaultEasyContext;

public class MavenSearchIocImpl extends DefaultEasyContext implements MavenSearchIoc {

    public MavenSearchIocImpl(ClassLoader classLoader, String... args) {
        super(classLoader, args);
    }
}