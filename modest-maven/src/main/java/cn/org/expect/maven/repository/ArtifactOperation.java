package cn.org.expect.maven.repository;

public interface ArtifactOperation {

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

    /**
     * 是否支持复制Maven依赖
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    default boolean supportCopyMavenDependency() {
        return true;
    }

    /**
     * 是否支持复制Gradle依赖
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    default boolean supportCopyGradleDependency() {
        return true;
    }

    /**
     * 是否支持浏览项目地址
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    default boolean supportOpenProjectURL() {
        return true;
    }

    /**
     * 是否支持浏览项目错误管理页面
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    default boolean supportOpenIssueURL() {
        return true;
    }

    /**
     * 是否支持打开 POM 文件
     *
     * @return 返回 true 表示支持，false 表示不支持
     */
    default boolean supportOpenPomFile() {
        return true;
    }
}
