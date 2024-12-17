package cn.org.expect.maven.repository.tencent;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.huawei.HuaweiArtifactDownloader;

@EasyBean(value = "download.use.tencent", priority = Integer.MAX_VALUE - 5)
public class TencentArtifactDownloader extends HuaweiArtifactDownloader {

    public TencentArtifactDownloader(EasyContext ioc) {
        super(ioc);
    }

    public String getAddress() {
        return "http://mirrors.cloud.tencent.com/nexus/repository/maven-public/";
    }
}
