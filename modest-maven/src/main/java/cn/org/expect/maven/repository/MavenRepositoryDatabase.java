package cn.org.expect.maven.repository;

/**
 * 数据库接口
 */
public interface MavenRepositoryDatabase {

    /**
     * 模糊搜索
     *
     * @param id 唯一编号
     * @return 搜索结果
     */
    MavenSearchResult select(String id);

    /**
     * 保存搜索结果
     *
     * @param id        唯一编号
     * @param resultSet 搜索结果
     */
    void insert(String id, MavenSearchResult resultSet);

    /**
     * 删除搜索结果
     *
     * @param id 唯一编号
     */
    void delete(String id);

    /**
     * 保存搜索结果
     *
     * @param groupId    域名
     * @param artifactId 工件ID
     * @param result     搜索结果
     */
    void insert(String groupId, String artifactId, MavenSearchResult result);

    /**
     * 查询搜索结果
     *
     * @param groupId    域名
     * @param artifactId 工件ID
     * @return 搜索结果
     */
    MavenSearchResult select(String groupId, String artifactId);

    /**
     * 删除所有数据
     */
    void clear();
}
