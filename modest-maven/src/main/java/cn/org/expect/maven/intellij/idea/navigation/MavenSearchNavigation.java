package cn.org.expect.maven.intellij.idea.navigation;

import javax.swing.*;

import cn.org.expect.maven.repository.MavenArtifact;
import com.intellij.navigation.NavigationItem;
import com.intellij.util.TextWithIcon;

/**
 * 查询结果导航结果中每条记录的接口
 */
public interface MavenSearchNavigation extends NavigationItem {

    /**
     * 设置导航记录左侧的图标
     *
     * @param icon 图标
     */
    void setLeftIcon(Icon icon);

    /**
     * 返回 Maven 工件信息
     *
     * @return Maven 工件信息
     */
    MavenArtifact getArtifact();

    /**
     * 返回导航记录右侧的图标与文本
     *
     * @return 图标与文本
     */
    TextWithIcon getRightIcon();

    /**
     * 返回唯一编号
     *
     * @return 整数
     */
    long getId();
}
