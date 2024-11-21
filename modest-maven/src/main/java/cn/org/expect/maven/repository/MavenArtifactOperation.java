package cn.org.expect.maven.repository;

public interface MavenArtifactOperation {

    /**
     * 是否支持在中央仓库中浏览
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    boolean supportOpenInCentralRepository();

    /**
     * 是否支持下载工件
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    boolean supportDownload();

    /**
     * 是否支持删除工件
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    boolean supportDelete();

    /**
     * 是否支持在本地操作系统上使用文件系统打开
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    boolean supportOpenInFileSystem();
}
