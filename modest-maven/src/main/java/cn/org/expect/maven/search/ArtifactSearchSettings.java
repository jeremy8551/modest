package cn.org.expect.maven.search;

import java.io.File;

import cn.org.expect.annotation.EasyBean;

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
     * @return 毫秒
     */
    long getInputIntervalTime();

    /**
     * 设置连续输入的间隔，单位毫秒
     *
     * @param inputIntervalTime 毫秒
     */
    void setInputIntervalTime(long inputIntervalTime);

    /**
     * 返回 Maven仓库ID，就是 {@linkplain EasyBean#value()}
     *
     * @return 标识，如: central、aliyun
     */
    ArtifactOption getRepositoryInfo();

    /**
     * Maven仓库ID，就是 {@linkplain EasyBean#value()}
     *
     * @param repositoryInfo Maven仓库ID
     */
    void setRepositoryInfo(ArtifactOption repositoryInfo);

    /**
     * 返回查询结果的超时时间，单位毫秒
     *
     * @return 超时时间
     */
    long getExpireTimeMillis();

    /**
     * 失效时间（单位毫秒）
     *
     * @param expireTimeMillis
     */
    void setExpireTimeMillis(long expireTimeMillis);
}
