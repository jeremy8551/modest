package cn.org.expect.intellij.idea.plugin.maven.settings;

import cn.org.expect.maven.search.ArtifactSearchMessage;
import org.jetbrains.annotations.NotNull;

public class SelectOption {

    private final String key;

    private final String name;

    public SelectOption(@NotNull String key) {
        this.key = key;
        this.name = ArtifactSearchMessage.getOptionName(key);
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.name;
    }
}
