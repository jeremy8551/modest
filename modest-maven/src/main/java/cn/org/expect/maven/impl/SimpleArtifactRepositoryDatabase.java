package cn.org.expect.maven.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

public class SimpleArtifactRepositoryDatabase implements ArtifactRepositoryDatabase {
    protected final static Log log = LogFactory.getLog(SimpleArtifactRepositoryDatabase.class);

    /** 线程池 */
    protected ExecutorService executorService;

    /** 模糊搜索词 pattern 与 {@linkplain ArtifactSearchResult} 的映射 */
    protected final Map<String, ArtifactSearchResult> patternMap;

    /** groupid、artifactId 与 {@linkplain ArtifactSearchResult} 的映射 */
    protected final Map<String, Map<String, ArtifactSearchResult>> extraMap;

    /** 序列化与反序列化工具 */
    protected final ArtifactRepositoryDatabaseEngine engine;

    public SimpleArtifactRepositoryDatabase(EasyContext ioc, Class<? extends ArtifactRepositoryDatabaseEngine> cls) {
        this.executorService = ioc.getBean(ThreadSource.class).getExecutorService();
        this.engine = Ensure.notNull(ioc.getBean(cls), cls.getName());
        this.patternMap = this.engine.getPattern();
        this.extraMap = this.engine.getArtifact();
    }

    public void insert(String pattern, ArtifactSearchResult result) {
        this.patternMap.put(pattern, result);
        this.store();
    }

    public ArtifactSearchResult select(String pattern) {
        return this.patternMap.get(pattern);
    }

    public void delete(String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            this.patternMap.remove(pattern);
            this.store();
        }
    }

    public void insert(String groupId, String artifactId, ArtifactSearchResult result) {
        Map<String, ArtifactSearchResult> group = this.extraMap.computeIfAbsent(groupId, k -> new HashMap<String, ArtifactSearchResult>());
        group.put(artifactId, result);
        this.store();
    }

    public ArtifactSearchResult select(String groupId, String artifactId) {
        Map<String, ArtifactSearchResult> map = this.extraMap.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }

    public void store() {
        this.executorService.execute(this.engine::save);
    }
}
