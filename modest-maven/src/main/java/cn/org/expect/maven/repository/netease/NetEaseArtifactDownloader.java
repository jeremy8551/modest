package cn.org.expect.maven.repository.netease;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.huawei.HuaweiArtifactDownloader;

@EasyBean(value = "download.use.netease", priority = Integer.MAX_VALUE - 4)
public class NetEaseArtifactDownloader extends HuaweiArtifactDownloader {

    public NetEaseArtifactDownloader(EasyContext ioc) {
        super(ioc);
    }

    public String getAddress() {
        return "http://mirrors.163.com/maven/repository/maven-public/";
    }
}
