package cn.org.expect.intellijidea.plugin.maven;

import java.util.List;

/**
 * Maven 工件集合
 */
public interface MavenArtifactSet {

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
}
