package cn.org.expect.maven.repository.aliyun;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.huawei.HuaweiArtifactDownloader;

@EasyBean(value = "download.use.aliyun", priority = Integer.MAX_VALUE - 2)
public class AliyunArtifactDownloader extends HuaweiArtifactDownloader {

    public AliyunArtifactDownloader(EasyContext ioc) {
        super(ioc);
    }

    public String getAddress() {
        return "https://maven.aliyun.com/repository/public";
    }
}
