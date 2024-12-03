package cn.org.expect.intellij.idea.plugin.maven.settings;

import cn.org.expect.maven.repository.ArtifactRepository;
import org.jetbrains.annotations.NotNull;

public class RepositorySelected {

    private final String id;

    private final String name;

    public RepositorySelected(@NotNull String id) {
        this.id = id;
        this.name = ArtifactRepository.getName(id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.name;
    }
}
