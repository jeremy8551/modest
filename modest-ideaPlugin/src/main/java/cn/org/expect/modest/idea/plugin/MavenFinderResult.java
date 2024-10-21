package cn.org.expect.modest.idea.plugin;

import java.util.List;

public class MavenFinderResult {

    private final String pattern;

    private final List<MavenArtifact> list;

    public MavenFinderResult(String pattern, List<MavenArtifact> list) {
        this.pattern = pattern;
        this.list = list;
    }

    public String getPattern() {
        return pattern;
    }

    public List<MavenArtifact> getArtifacts() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }
}
