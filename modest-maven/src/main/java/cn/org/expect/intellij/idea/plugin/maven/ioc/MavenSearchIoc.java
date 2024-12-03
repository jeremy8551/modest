package cn.org.expect.intellij.idea.plugin.maven.ioc;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.settings.SelectOption;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.ArtifactRepository;

public interface MavenSearchIoc extends EasyContext {

    /**
     * 已注册工件仓库的数组
     *
     * @return 仓库数组
     */
    default SelectOption[] getRepositorySelectOptions() {
        List<EasyBeanInfo> list = this.getBeanInfoList(ArtifactRepository.class).stream().sorted((b1, b2) -> b2.getPriority() - b1.getPriority()).toList();
        int size = list.size();
        SelectOption[] array = new SelectOption[size];
        for (int i = 0; i < size; i++) {
            EasyBeanInfo beanInfo = list.get(i);
            array[i] = new SelectOption(beanInfo.getName());
        }
        return array;
    }
}
