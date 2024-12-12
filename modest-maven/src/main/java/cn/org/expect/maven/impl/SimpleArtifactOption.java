package cn.org.expect.maven.impl;

import cn.org.expect.maven.ArtifactOption;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.util.Ensure;

public class SimpleArtifactOption implements ArtifactOption {

    private final String key;

    private final String name;

    public SimpleArtifactOption(String key) {
        this.key = Ensure.notNull(key);
        this.name = MavenMessage.get("maven.search.repository.option." + key);
    }

    public String value() {
        return key;
    }

    public String getDisplayName() {
        return name;
    }

    public boolean equals(Object obj) {
        return obj instanceof ArtifactOption && ((ArtifactOption) obj).value().equals(this.value());
    }

    public String toString() {
        return this.name;
    }
}
