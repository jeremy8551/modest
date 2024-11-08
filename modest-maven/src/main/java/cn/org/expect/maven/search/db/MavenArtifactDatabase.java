package cn.org.expect.maven.search.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.util.StringUtils;

public class MavenArtifactDatabase {

    /**
     * 模糊搜索词 pattern 与 {@linkplain MavenSearchResult} 的映射
     */
    protected final Map<String, MavenSearchResult> patternMap;

    /**
     * groupid、artifactId 与 {@linkplain MavenSearchResult} 的映射
     */
    protected final Map<String, Map<String, MavenSearchResult>> extraMap;

    public MavenArtifactDatabase() {
        this.patternMap = new ConcurrentHashMap<String, MavenSearchResult>();
        this.extraMap = new ConcurrentHashMap<String, Map<String, MavenSearchResult>>();
    }

    public void insert(String pattern, MavenSearchResult resultSet) {
        this.patternMap.put(pattern, resultSet);
    }

    public MavenSearchResult select(String pattern) {
        return this.patternMap.get(pattern);
    }

    public void insert(String groupId, String artifactId, MavenSearchResult set) {
        Map<String, MavenSearchResult> group = this.extraMap.computeIfAbsent(groupId, k -> new HashMap<String, MavenSearchResult>());
        group.put(artifactId, set);
    }

    public MavenSearchResult select(String groupId, String artifactId) {
        Map<String, MavenSearchResult> map = this.extraMap.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }

    public void delete(String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            this.patternMap.remove(pattern);
        }
    }

    public void clear() {
        this.patternMap.clear();
        this.extraMap.clear();
    }
}
