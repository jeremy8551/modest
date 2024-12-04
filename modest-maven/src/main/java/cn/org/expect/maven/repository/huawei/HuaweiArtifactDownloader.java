package cn.org.expect.maven.repository.huawei;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.impl.AbstractArtifactDownloader;

@EasyBean(value = "download.use.huawei", priority = Integer.MAX_VALUE - 3)
public class HuaweiArtifactDownloader extends AbstractArtifactDownloader {

    public HuaweiArtifactDownloader(EasyContext ioc) {
        super(ioc);
    }

    public String getListAddress() {
        return "https://repo.huaweicloud.com/repository/maven/";
    }

    public String getAddress() {
        return this.getListAddress();
    }
}
