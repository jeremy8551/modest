package cn.org.expect.maven.repository;

import java.util.Map;

/**
 * 仓库存储引擎
 */
public interface ArtifactRepositoryDatabaseEngine {

    /**
     * 返回模糊搜索结果的集合
     *
     * @return 模糊搜索结果的集合
     */
    Map<String, ArtifactSearchResult> getPattern();

    /**
     * 返回精确搜索结果的集合
     *
     * @return 精确搜索结果的集合
     */
    Map<String, Map<String, ArtifactSearchResult>> getArtifact();

    /**
     * 清空所有数据
     */
    void clear();

    /**
     * 保存数据
     */
    void save();
}
