package cn.org.expect.maven.concurrent;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchRepaintJob;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.maven.search.MavenSearchUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import org.jetbrains.annotations.Nullable;

public class MavenSearchPatternJob extends MavenSearchJob {

    /** 模糊搜索的文本 */
    protected final String pattern;

    /** true表示先删除数据库中的记录再执行搜索，false表示直接执行搜索 */
    protected boolean delete;

    public MavenSearchPatternJob(String pattern, boolean delete) {
        super();
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

        MavenSearch search = this.getSearch();
        MavenSearchResult result = this.query(search, this.pattern);
        if (this.terminate) {
            return 0;
        }

        // 查询失败
        if (result == null) {
            String message = MavenSearchMessage.get("maven.search.send.url.fail");
            search.setProgress(message);
            search.setStatusBar(MavenSearchAdvertiser.ERROR, message);
            return 0;
        }

        // 查询结果为空
        if (result.size() == 0) {
            String message = MavenSearchMessage.get("maven.search.noting.found");
            search.setProgress(message);
            search.setStatusBar(MavenSearchAdvertiser.NORMAL, message);
            return 0;
        }

        // 保存查询结果
        result.reset();
        search.getContext().setSearchResult(result);
        search.execute(new MavenSearchRepaintJob());
        return 0;
    }

    public MavenSearchResult query(MavenSearch search, String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return null;
        }

        pattern = StringUtils.trimBlank(pattern);
        search.getContext().setSearchText(pattern);
        MavenRepositoryDatabase database = search.getDatabase();
        if (this.delete) {
            database.delete(pattern);
        }

        try {
            MavenSearchResult result = database.select(pattern);
            if (result == null || result.size() == 0 || result.isExpire(search.getSettings().getExpireTimeMillis())) {
                if (MavenSearchUtils.isExtraSearch(pattern)) {
                    return this.queryExtra(database, pattern); // 精确搜索
                } else {
                    return this.queryPattern(database, pattern); // 模糊搜索
                }
            }
            return result;
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * 模糊搜索
     *
     * @param database 数据库接口
     * @param pattern  文本信息
     * @return 搜索结果
     * @throws Exception 发送错误
     */
    private @Nullable MavenSearchResult queryPattern(MavenRepositoryDatabase database, String pattern) throws Exception {
        MavenSearchResult resultSet = this.getRemoteRepository().query(pattern, 1);
        if (resultSet != null) {
            database.insert(pattern, resultSet);
        }
        return resultSet;
    }

    /**
     * 精确搜索
     *
     * @param database 数据库接口
     * @param pattern  搜索文本
     * @return 搜索结果
     * @throws Exception 发生错误
     */
    private @Nullable MavenSearchResult queryExtra(MavenRepositoryDatabase database, String pattern) throws Exception {
        String[] array = StringUtils.split(pattern, ':');
        String groupId = array[0];
        String artifactId = array[1];

        // 精确搜索
        MavenSearchResult result = this.getRemoteRepository().query(groupId, artifactId);
        if (result == null) {
            return null;
        }

        // 保存搜索结果
        database.insert(groupId, artifactId, result);

        // 如果有多个结果
        List<MavenArtifact> list = new ArrayList<>();
        List<MavenArtifact> artifacts = result.getList();
        if (!artifacts.isEmpty()) {
            list.add(artifacts.get(0));
        }

        // 保存模糊搜索结果
        SimpleMavenSearchResult patternResult = new SimpleMavenSearchResult(list, 1, 1, System.currentTimeMillis());
        database.insert(pattern, patternResult);
        return patternResult;
    }
}
