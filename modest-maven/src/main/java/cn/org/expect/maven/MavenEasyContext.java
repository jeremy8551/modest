package cn.org.expect.maven;

import java.util.List;

import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.impl.SimpleArtifactOption;
import cn.org.expect.maven.repository.ArtifactDownloader;
import cn.org.expect.maven.repository.ArtifactRepository;

public interface MavenEasyContext extends EasyContext {

    /**
     * 已注册工件仓库的数组
     *
     * @return 仓库数组
     */
    default MavenOption[] getRepositoryOptions() {
        List<EasyBeanInfo> list = this.getBeanInfoList(ArtifactRepository.class).stream().sorted((b1, b2) -> b2.getPriority() - b1.getPriority()).toList();
        int size = list.size();
        MavenOption[] array = new MavenOption[size];
        for (int i = 0; i < size; i++) {
            EasyBeanInfo beanInfo = list.get(i);
            array[i] = new SimpleArtifactOption(beanInfo.getName());
        }
        return array;
    }

    /**
     * 已注册工件仓库的数组
     *
     * @return 仓库数组
     */
    default MavenOption[] getDownloaderOptions() {
        List<EasyBeanInfo> list = this.getBeanInfoList(ArtifactDownloader.class).stream().sorted((b1, b2) -> b2.getPriority() - b1.getPriority()).toList();
        int size = list.size();
        MavenOption[] array = new MavenOption[size];
        for (int i = 0; i < size; i++) {
            EasyBeanInfo beanInfo = list.get(i);
            array[i] = new SimpleArtifactOption(beanInfo.getName());
        }
        return array;
    }
}
