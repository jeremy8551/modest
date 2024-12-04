package cn.org.expect.maven.repository;

import java.io.File;
import java.util.List;

import cn.org.expect.concurrent.Terminate;
import cn.org.expect.maven.search.ArtifactSearchAware;

public interface ArtifactDownloader extends Terminate, ArtifactSearchAware {

    /**
     * 下载工件
     *
     * @param artifact
     * @param parent
     * @param downloadSources
     * @param downloadDocs
     * @param downloadAnnotation
     * @return
     * @throws Exception
     */
    List<File> execute(Artifact artifact, File parent, boolean downloadSources, boolean downloadDocs, boolean downloadAnnotation) throws Exception;
}
