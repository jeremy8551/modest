package cn.org.expect.maven;

import java.util.Set;

import cn.org.expect.util.ResourceMessageBundle;

public class MavenResourceBundle implements ResourceMessageBundle {

    public MavenResourceBundle() {
    }

    public boolean contains(String key) {
        return MavenMessage.contains(key);
    }

    public String get(String key) {
        return MavenMessage.getResourceBundle(key);
    }

    public Set<String> getKeys() {
        return MavenMessage.getKeys();
    }
}
