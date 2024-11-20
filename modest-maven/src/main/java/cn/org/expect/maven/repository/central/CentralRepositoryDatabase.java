package cn.org.expect.maven.repository.central;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

public class CentralRepositoryDatabase implements MavenRepositoryDatabase {
    protected final static Log log = LogFactory.getLog(CentralRepositoryDatabase.class);

    /** 线程池 */
    protected ExecutorService executorService;

    /** 模糊搜索词 pattern 与 {@linkplain MavenSearchResult} 的映射 */
    protected final Map<String, MavenSearchResult> patternMap;

    /** groupid、artifactId 与 {@linkplain MavenSearchResult} 的映射 */
    protected final Map<String, Map<String, MavenSearchResult>> extraMap;

    /** 序列化与反序列化工具 */
    protected final CentralRepositoryDatabaseSerializer serializer;

    public CentralRepositoryDatabase(ThreadSource threadSource) {
        this.patternMap = new ConcurrentHashMap<>();
        this.extraMap = new ConcurrentHashMap<>();
        this.executorService = threadSource.getExecutorService();
        this.serializer = new CentralRepositoryDatabaseSerializer(Settings.getUserHome(), this.patternMap, this.extraMap);
    }

    public void insert(String id, MavenSearchResult resultSet) {
        this.patternMap.put(id, resultSet);
        this.store();
    }

    public MavenSearchResult select(String id) {
        return this.patternMap.get(id);
    }

    public void insert(String groupId, String artifactId, MavenSearchResult result) {
        Map<String, MavenSearchResult> group = this.extraMap.computeIfAbsent(groupId, k -> new HashMap<String, MavenSearchResult>());
        group.put(artifactId, result);
        this.store();
    }

    public MavenSearchResult select(String groupId, String artifactId) {
        Map<String, MavenSearchResult> map = this.extraMap.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }

    public void delete(String id) {
        if (StringUtils.isNotBlank(id)) {
            this.patternMap.remove(id);
            this.store();
        }
    }

    public void clear() {
        this.patternMap.clear();
        this.extraMap.clear();
        this.store();
    }

    public void store() {
        this.executorService.execute(() -> this.serializer.save(this.patternMap, this.extraMap));
    }
}
