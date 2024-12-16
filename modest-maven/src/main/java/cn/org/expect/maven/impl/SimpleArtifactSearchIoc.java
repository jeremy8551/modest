package cn.org.expect.maven.impl;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.maven.MavenEasyContext;

public class SimpleArtifactSearchIoc extends DefaultEasyContext implements MavenEasyContext {

    public SimpleArtifactSearchIoc(ClassLoader classLoader, String... args) {
        super(classLoader, args);
    }
}
