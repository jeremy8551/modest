package cn.org.expect.maven.repository.local;

import java.io.File;

/**
 * 本地仓库配置信息
 */
public interface LocalRepositorySettings {

    /**
     * 返回本地仓库的目录
     *
     * @return 目录
     */
    File getRepository();

    /**
     * 设置本地仓库目录
     *
     * @param dir 目录
     */
    void setRepository(File dir);

    /**
     * 是否支持下载源代码
     *
     * @return 返回true表示下载源代码，false表示不下载源代码
     */
    boolean isDownloadSourcesAutomatically();

    /**
     * 设置是否支持下载源代码
     *
     * @param value 返回true表示下载源代码，false表示不下载源代码
     */
    void setDownloadSourcesAutomatically(boolean value);

    /**
     * 是否支持下载文档
     *
     * @return 返回true表示支持下载文档，false表示不下载文档
     */
    boolean isDownloadDocsAutomatically();

    /**
     * 设置是否支持下载文档
     *
     * @param value 返回true表示支持下载文档，false表示不下载文档
     */
    void setDownloadDocsAutomatically(boolean value);

    /**
     * 是否下载注解功能
     *
     * @return 返回true表示下载注解功能，false表示不下载注解功能
     */
    boolean isDownloadAnnotationsAutomatically();

    /**
     * 设置是否下载注解功能
     *
     * @param value 返回true表示下载注解功能，false表示不下载注解功能
     */
    void setDownloadAnnotationsAutomatically(boolean value);
}
