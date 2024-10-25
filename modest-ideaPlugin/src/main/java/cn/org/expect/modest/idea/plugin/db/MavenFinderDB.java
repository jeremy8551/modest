package cn.org.expect.modest.idea.plugin.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;

public class MavenFinderDB {

    public final static MavenFinderDB INSTANCE = new MavenFinderDB();

    /**
     * 模糊搜索词 pattern 与 MavenFinderResult 的映射
     */
    protected final Map<String, MavenFinderResult> patternMap;

    /**
     * groupid、artifactId 与 MavenFinderResult 的映射
     */
    protected final Map<String, Map<String, MavenFinderResult>> extraMap;

    private MavenFinderDB() {
        this.patternMap = new ConcurrentHashMap<String, MavenFinderResult>();
        this.extraMap = new ConcurrentHashMap<String, Map<String, MavenFinderResult>>();
    }

    public MavenFinderResult insert(String pattern, List<MavenArtifact> list) {
        MavenFinderResult result = new MavenFinderResult(pattern, list);
        this.insert(result);
        return result;
    }

    public void insert(MavenFinderResult result) {
        this.patternMap.put(result.getPattern(), result);
    }

    public MavenFinderResult select(String pattern) {
        return this.patternMap.get(pattern);
    }

    public MavenFinderResult insert(String groupId, String artifactId, List<MavenArtifact> list) {
        MavenFinderResult result = new MavenFinderResult("", list);
        Map<String, MavenFinderResult> group = this.extraMap.computeIfAbsent(groupId, k -> new HashMap<>());
        group.put(artifactId, result);
        return result;
    }

    public MavenFinderResult select(String groupId, String artifactId) {
        Map<String, MavenFinderResult> map = this.extraMap.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }
}
