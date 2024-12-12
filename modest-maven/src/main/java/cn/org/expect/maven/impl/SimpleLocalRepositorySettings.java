package cn.org.expect.maven.impl;

import java.io.File;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.repository.local.LocalRepositorySettings;
import cn.org.expect.util.Ensure;

@EasyBean(singleton = true)
public class SimpleLocalRepositorySettings implements LocalRepositorySettings {

    /** 本地仓库目录 */
    private volatile File repository;

    /** 是否支持下载源代码 */
    private volatile boolean downloadSourcesAutomatically;

    /** 是否支持下载文档 */
    private volatile boolean downloadDocsAutomatically;

    /** 是否下载注解功能 */
    private volatile boolean downloadAnnotationsAutomatically;

    public SimpleLocalRepositorySettings() {
    }

    public File getRepository() {
        return this.repository;
    }

    public void setRepository(File dir) {
        this.repository = Ensure.notNull(dir);
    }

    public boolean isDownloadSourcesAutomatically() {
        return downloadSourcesAutomatically;
    }

    public void setDownloadSourcesAutomatically(boolean value) {
        this.downloadSourcesAutomatically = value;
    }

    public boolean isDownloadDocsAutomatically() {
        return downloadDocsAutomatically;
    }

    public void setDownloadDocsAutomatically(boolean value) {
        this.downloadDocsAutomatically = value;
    }

    public boolean isDownloadAnnotationsAutomatically() {
        return downloadAnnotationsAutomatically;
    }

    public void setDownloadAnnotationsAutomatically(boolean value) {
        this.downloadAnnotationsAutomatically = value;
    }
}
