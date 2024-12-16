package cn.org.expect.maven;

/**
 * 获取工件的接口
 */
public interface ArtifactGetter {

    /**
     * 返回工件
     *
     * @return 工件信息
     */
    Artifact getArtifact();
}
