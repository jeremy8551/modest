package cn.org.expect.maven.concurrent;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.search.ArtifactSearch;
import cn.org.expect.maven.search.ArtifactSearchStatusMessageType;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import org.jetbrains.annotations.Nullable;

public class ArtifactSearchPatternJob extends ArtifactSearchJob {

    /** 模糊搜索的文本 */
    protected final String pattern;

    /** true表示先删除数据库中的记录再执行搜索，false表示直接执行搜索 */
    protected boolean delete;

    public ArtifactSearchPatternJob(String pattern, boolean delete) {
        super("maven.search.job.search.pattern.description", pattern);
        this.pattern = Ensure.notNull(pattern);
        this.delete = delete;
    }

    public String getPattern() {
        return this.pattern;
    }

    public int execute() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} search pattern: {}", this.getName(), this.pattern);
        }

        if (StringUtils.isBlank(this.pattern)) {
            return -1;
        }

        ArtifactSearch search = this.getSearch();
        ArtifactSearchResult result = this.query(search, this.pattern);
        if (this.terminate) {
            return 0;
        } else {
            search.getContext().setSearchResult(result);
        }

        // 查询失败
        if (result == null) {
            search.display();
            search.setProgress("maven.search.send.url.fail", search.getRepositoryInfo().getDisplayName());
            search.setStatusBar(ArtifactSearchStatusMessageType.ERROR, "maven.search.send.url.fail", search.getRepositoryInfo().getDisplayName());
            return 0;
        }

        // 查询结果为空
        if (result.size() == 0) {
            search.display();
            search.setProgress("maven.search.nothing.found");
            search.setStatusBar(ArtifactSearchStatusMessageType.NORMAL, "maven.search.nothing.found");
            return 0;
        }

        search.asyncDisplay();
        return 0;
    }

    public ArtifactSearchResult query(ArtifactSearch search, String pattern) throws Exception {
        pattern = StringUtils.trimBlank(pattern);
        search.getContext().setSearchText(pattern);
        ArtifactRepositoryDatabase database = search.getDatabase();
        if (this.delete) {
            database.delete(pattern);
        }

        ArtifactSearchResult result = database.select(pattern);
        if (result == null || result.size() == 0 || result.isExpire(search.getSettings().getExpireTimeMillis())) {
            if (search.getPattern().isExtra(pattern)) {
                return this.queryExtra(database, pattern); // 精确搜索
            } else {
                return this.queryPattern(database, pattern); // 模糊搜索
            }
        }
        return result;
    }

    /**
     * 模糊搜索
     *
     * @param database 数据库接口
     * @param pattern  文本信息
     * @return 搜索结果
     * @throws Exception 发送错误
     */
    private @Nullable ArtifactSearchResult queryPattern(ArtifactRepositoryDatabase database, String pattern) throws Exception {
        ArtifactSearchResult result = this.getRemoteRepository().query(pattern, 1);
        if (result != null) {
            database.insert(pattern, result);
        }
        return result;
    }

    /**
     * 精确搜索
     *
     * @param database 数据库接口
     * @param pattern  搜索文本
     * @return 搜索结果
     * @throws Exception 发生错误
     */
    private @Nullable ArtifactSearchResult queryExtra(ArtifactRepositoryDatabase database, String pattern) throws Exception {
        String[] array = StringUtils.split(pattern, ':');
        String groupId = array[0];
        String artifactId = array[1];

        // 精确搜索
        ArtifactSearchResult result = this.getRemoteRepository().query(groupId, artifactId);
        if (result == null) {
            return null;
        }

        // 保存搜索结果
        database.insert(groupId, artifactId, result);

        // 如果有多个结果
        List<Artifact> list = new ArrayList<>();
        List<Artifact> artifacts = result.getList();
        if (!artifacts.isEmpty()) {
            list.add(artifacts.get(0));
        }

        // 保存模糊搜索结果
        SimpleArtifactSearchResult patternResult = new SimpleArtifactSearchResult(result.getType(), list, 1, 1, System.currentTimeMillis(), false);
        database.insert(pattern, patternResult);
        return patternResult;
    }
}
