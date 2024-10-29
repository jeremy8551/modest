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
    List<MavenArtifact> getArtifacts();

    /**
     * Maven 工件个数
     *
     * @return 工件个数
     */
    int size();
}
