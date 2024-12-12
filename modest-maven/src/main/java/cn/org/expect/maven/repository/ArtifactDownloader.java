package cn.org.expect.maven.repository;

import java.io.File;

import cn.org.expect.concurrent.Terminate;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.search.ArtifactSearchAware;

public interface ArtifactDownloader extends Terminate, ArtifactSearchAware {

    /**
     * 下载工件
     *
     * @param artifact           工件信息
     * @param parent             下载后文件存储的目录
     * @param downloadSources    是否下载源文件
     * @param downloadDocs       是否下载文档
     * @param downloadAnnotation 是否下载注解
     * @throws Exception 下载工件发生错误
     */
    void execute(Artifact artifact, File parent, boolean downloadSources, boolean downloadDocs, boolean downloadAnnotation) throws Exception;
}
