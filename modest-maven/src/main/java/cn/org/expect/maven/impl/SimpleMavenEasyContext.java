package cn.org.expect.maven.impl;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.maven.MavenEasyContext;

public class SimpleMavenEasyContext extends DefaultEasyContext implements MavenEasyContext {

    public SimpleMavenEasyContext(ClassLoader classLoader, String... args) {
        super(classLoader, args);
    }
}
