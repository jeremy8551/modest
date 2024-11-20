package cn.org.expect.maven.repository;

import java.util.List;

/**
 * Maven 仓库搜索结果
 */
public interface MavenSearchResult {

    /**
     * Maven 工件列表
     *
     * @return 集合
     */
    List<MavenArtifact> getList();

    /**
     * 下次查询的起始位置
     *
     * @return 位置信息，从1开始
     */
    int getStart();

    /**
     * 返回总记录数
     *
     * @return 总记录数
     */
    int getFoundNumber();

    /**
     * Maven 工件个数
     *
     * @return 工件个数
     */
    int size();

    /**
     * 判断工件是否存在
     *
     * @param groupId    工件域名
     * @param artifactId 工件ID
     * @param version    版本号
     * @return 返回true表示存在
     */
    boolean contains(String groupId, String artifactId, String version);

    /**
     * 添加一个工件
     *
     * @param artifact 工件
     * @return 返回true表示添加成功 false表示失败
     */
    boolean addArtifact(MavenArtifact artifact);
}
