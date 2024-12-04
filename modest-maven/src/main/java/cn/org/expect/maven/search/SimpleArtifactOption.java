package cn.org.expect.maven.search;

import cn.org.expect.util.Ensure;

public class SimpleArtifactOption implements ArtifactOption {

    private final String key;

    private final String name;

    public SimpleArtifactOption(String key) {
        this.key = Ensure.notNull(key);
        this.name = ArtifactSearchMessage.get("maven.search.repository.option." + key);
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object obj) {
        return obj instanceof ArtifactOption && ((ArtifactOption) obj).getKey().equals(this.getKey());
    }

    public String toString() {
        return this.name;
    }
}
