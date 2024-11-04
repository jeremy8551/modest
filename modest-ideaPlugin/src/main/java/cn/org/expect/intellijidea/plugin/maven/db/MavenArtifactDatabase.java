package cn.org.expect.intellijidea.plugin.maven.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.intellijidea.plugin.maven.MavenSearchResult;

public class MavenArtifactDatabase {

    /**
     * 模糊搜索词 pattern 与 MavenFinderResult 的映射
     */
    protected final Map<String, MavenSearchResult> patternMap;

    /**
     * groupid、artifactId 与 MavenFinderResult 的映射
     */
    protected final Map<String, Map<String, MavenSearchResult>> extraMap;

    public MavenArtifactDatabase() {
        this.patternMap = new ConcurrentHashMap<String, MavenSearchResult>();
        this.extraMap = new ConcurrentHashMap<String, Map<String, MavenSearchResult>>();
    }

    public MavenSearchResult insert(String pattern, MavenSearchResult resultSet) {
        this.patternMap.put(pattern, resultSet);
        return resultSet;
    }

    public MavenSearchResult select(String pattern) {
        return this.patternMap.get(pattern);
    }

    public MavenSearchResult insert(String groupId, String artifactId, MavenSearchResult set) {
        Map<String, MavenSearchResult> group = this.extraMap.computeIfAbsent(groupId, k -> new HashMap<String, MavenSearchResult>());
        group.put(artifactId, set);
        return set;
    }

    public MavenSearchResult select(String groupId, String artifactId) {
        Map<String, MavenSearchResult> map = this.extraMap.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }

    public MavenSearchResult delete(String pattern) {
        return this.patternMap.remove(pattern);
    }

    public void clear() {
        this.patternMap.clear();
        this.extraMap.clear();
    }
}
