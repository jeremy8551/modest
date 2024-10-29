package cn.org.expect.intellijidea.plugin.maven.impl;

import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenArtifactSet;
import cn.org.expect.util.Ensure;

public class MavenArtifactArrayList implements MavenArtifactSet {

    private final List<MavenArtifact> list;

    public MavenArtifactArrayList(List<MavenArtifact> list) {
        this.list = Ensure.notNull(list);
    }

    public List<MavenArtifact> getArtifacts() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }
}
