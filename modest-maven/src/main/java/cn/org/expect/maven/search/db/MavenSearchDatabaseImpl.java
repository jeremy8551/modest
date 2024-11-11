package cn.org.expect.maven.search.db;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

public class MavenSearchDatabaseImpl implements MavenSearchDatabase {
    private final static Log log = LogFactory.getLog(MavenSearchDatabaseImpl.class);

    /** 本地仓库 */
    private final LocalRepository localRepository;

    /** 模糊搜索词 pattern 与 {@linkplain MavenSearchResult} 的映射 */
    protected final Map<String, MavenSearchResult> patternMap;

    /** groupid、artifactId 与 {@linkplain MavenSearchResult} 的映射 */
    protected final Map<String, Map<String, MavenSearchResult>> extraMap;

    /** 序列化与反序列化工具 */
    protected final DatabaseSerializer serializer;

    public MavenSearchDatabaseImpl(LocalRepository repository) {
        this.localRepository = Ensure.notNull(repository);
        this.patternMap = new ConcurrentHashMap<String, MavenSearchResult>();
        this.extraMap = new ConcurrentHashMap<String, Map<String, MavenSearchResult>>();
        this.serializer = new DatabaseSerializer(new File(repository.getAddress()), this.patternMap, this.extraMap);
    }

    @Override
    public void insert(String id, MavenSearchResult resultSet) {
        this.patternMap.put(id, resultSet);
        this.store();
    }

    @Override
    public MavenSearchResult select(String id) {
        return this.patternMap.get(id);
    }

    @Override
    public void insert(String groupId, String artifactId, MavenSearchResult result) {
        Map<String, MavenSearchResult> group = this.extraMap.computeIfAbsent(groupId, k -> new HashMap<String, MavenSearchResult>());
        group.put(artifactId, result);
        this.store();
    }

    @Override
    public MavenSearchResult select(String groupId, String artifactId) {
        Map<String, MavenSearchResult> map = this.extraMap.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }

    @Override
    public void delete(String id) {
        if (StringUtils.isNotBlank(id)) {
            this.patternMap.remove(id);
            this.store();
        }
    }

    @Override
    public void clear() {
        this.patternMap.clear();
        this.extraMap.clear();
        this.store();
    }

    public void store() {
        this.serializer.save(new File(this.localRepository.getAddress()), this.patternMap, this.extraMap);
    }
}
