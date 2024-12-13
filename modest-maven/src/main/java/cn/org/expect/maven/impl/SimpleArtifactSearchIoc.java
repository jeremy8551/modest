package cn.org.expect.maven.impl;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.maven.ArtifactSearchIoc;

public class SimpleArtifactSearchIoc extends DefaultEasyContext implements ArtifactSearchIoc {

    public SimpleArtifactSearchIoc(ClassLoader classLoader, String... args) {
        super(classLoader, args);
    }
}
