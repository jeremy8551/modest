package cn.org.expect.intellij.idea.plugin.maven.ioc;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchScope;
import cn.org.expect.intellij.idea.plugin.maven.settings.RepositorySelected;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.ArtifactRepository;

public interface MavenSearchIoc extends EasyContext {

    /**
     * 已注册工件仓库的数组
     *
     * @return 仓库数组
     */
    default MavenSearchScope[] getScopes() {
        RepositorySelected[] array = getRepositorySelectList();
        MavenSearchScope[] result = new MavenSearchScope[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new MavenSearchScope(array[i]);
        }
        return result;
    }

    default RepositorySelected[] getRepositorySelectList() {
        List<EasyBeanInfo> list = this.getBeanInfoList(ArtifactRepository.class).stream().sorted((b1, b2) -> b2.getPriority() - b1.getPriority()).toList();
        int size = list.size();
        RepositorySelected[] array = new RepositorySelected[size];
        for (int i = 0; i < size; i++) {
            EasyBeanInfo beanInfo = list.get(i);
            array[i] = new RepositorySelected(beanInfo.getName());
        }
        return array;
    }
}
