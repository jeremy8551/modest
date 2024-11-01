package cn.org.expect.intellijidea.plugin.maven.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenArtifactSet;
import cn.org.expect.intellijidea.plugin.maven.impl.MavenArtifactArrayList;

public class MavenArtifactDatabase {

    /**
     * 模糊搜索词 pattern 与 MavenFinderResult 的映射
     */
    protected final Map<String, MavenArtifactSet> patternMap;

    /**
     * groupid、artifactId 与 MavenFinderResult 的映射
     */
    protected final Map<String, Map<String, MavenArtifactSet>> extraMap;

    public MavenArtifactDatabase() {
        this.patternMap = new ConcurrentHashMap<String, MavenArtifactSet>();
        this.extraMap = new ConcurrentHashMap<String, Map<String, MavenArtifactSet>>();
    }

    public MavenArtifactSet insert(String pattern, List<MavenArtifact> list) {
        MavenArtifactSet set = new MavenArtifactArrayList(list);
        this.patternMap.put(pattern, set);
        return set;
    }

    public MavenArtifactSet select(String pattern) {
        return this.patternMap.get(pattern);
    }

    public MavenArtifactSet insert(String groupId, String artifactId, List<MavenArtifact> list) {
        MavenArtifactSet set = new MavenArtifactArrayList(list);
        Map<String, MavenArtifactSet> group = this.extraMap.computeIfAbsent(groupId, k -> new HashMap<String, MavenArtifactSet>());
        group.put(artifactId, set);
        return set;
    }

    public MavenArtifactSet select(String groupId, String artifactId) {
        Map<String, MavenArtifactSet> map = this.extraMap.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }

    public MavenArtifactSet delete(String pattern) {
        return this.patternMap.remove(pattern);
    }

    public void clear() {
        this.patternMap.clear();
        this.extraMap.clear();
    }
}
