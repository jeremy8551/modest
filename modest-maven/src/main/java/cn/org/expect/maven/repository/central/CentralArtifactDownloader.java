package cn.org.expect.maven.repository.central;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.impl.AbstractArtifactDownloader;

@EasyBean(value = "central", priority = Integer.MAX_VALUE - 1)
public class CentralArtifactDownloader extends AbstractArtifactDownloader {

    public CentralArtifactDownloader(EasyContext ioc) {
        super(ioc);
    }

    public String getAddress() {
        return "https://repo1.maven.org/maven2/";
    }
}
