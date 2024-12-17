package cn.org.expect.maven.search;

import java.io.File;

/**
 * 搜索配置信息
 */
public interface ArtifactSearchSettings {

    /**
     * 设置插件名
     *
     * @param name 插件名
     */
    void setName(String name);

    /**
     * 返回插件名
     *
     * @return 插件名
     */
    String getName();

    /**
     * 设置工作目录
     *
     * @param file 工作目录
     */
    void setWorkHome(File file);

    /**
     * 返回工作目录
     *
     * @return 工作目录
     */
    File getWorkHome();

    /**
     * 返回连续输入的间隔，单位毫秒
     *
     * @return 毫秒数
     */
    long getInputIntervalTime();

    /**
     * 设置连续输入的间隔，单位毫秒
     *
     * @param millis 毫秒数
     */
    void setInputIntervalTime(long millis);

    /**
     * 返回仓库ID
     *
     * @return 仓库ID
     */
    String getRepositoryId();

    /**
     * 仓库ID
     *
     * @param repositoryId 仓库ID
     */
    void setRepositoryId(String repositoryId);

    /**
     * 返回查询结果的过期时间，搜索结果过期后自动失效
     *
     * @return 毫秒数
     */
    long getExpireTimeMillis();

    /**
     * 失效时间（单位毫秒）
     *
     * @param millis 毫秒数
     */
    void setExpireTimeMillis(long millis);

    /**
     * 下载工件的方式
     *
     * @return 下载方式
     */
    String getDownloadWay();

    /**
     * 设置下载工件的方式
     *
     * @param downSource 下载方式
     */
    void setDownloadWay(String downSource);
}
