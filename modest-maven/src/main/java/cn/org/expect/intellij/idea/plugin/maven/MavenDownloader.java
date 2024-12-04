package cn.org.expect.intellij.idea.plugin.maven;

import java.io.File;
import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.impl.AbstractArtifactDownloader;

@EasyBean(value = "download.use.maven", priority = Integer.MAX_VALUE)
public class MavenDownloader extends AbstractArtifactDownloader {

    public MavenDownloader(EasyContext ioc) {
        super(ioc);
    }

    public String getAddress() {
        return "";
    }

    public List<File> execute(Artifact artifact, File parent, boolean downloadSources, boolean downloadDocs, boolean downloadAnnotation) throws Exception {
        if (IdeaMavenUtils.hasSetupMavenPlugin()) {
            IdeaMavenUtils.download((MavenSearchPlugin) this.search, artifact);
        }
        return null;
    }
}
