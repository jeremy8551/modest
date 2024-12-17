package cn.org.expect.maven.repository.huawei;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.maven.repository.AbstractArtifactDownloader;

@EasyBean(value = "download.use.huawei", priority = Integer.MAX_VALUE - 3)
public class HuaweiArtifactDownloader extends AbstractArtifactDownloader {

    public HuaweiArtifactDownloader(EasyContext ioc) {
        super(ioc);
    }

    /**
     * 访问频繁就不稳定，被怀疑为攻击导致被限流
     *
     * @return 地址
     */
    public String getListAddress() {
        return "https://repo.huaweicloud.com/repository/maven/";
    }
    
    public String getAddress() {
        return this.getListAddress();
    }
}
