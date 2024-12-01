package cn.org.expect.intellij.idea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.maven.repository.Artifact;
import com.intellij.navigation.NavigationItem;
import com.intellij.util.TextWithIcon;

/**
 * 查询结果导航结果中每条记录的接口
 */
public interface MavenSearchNavigation extends NavigationItem {

    /**
     * 设置导航记录（左侧或右侧）的图标
     *
     * @param icon 图标
     */
    void setIcon(Icon icon);

    /**
     * 返回 Maven 工件信息
     *
     * @return Maven 工件信息
     */
    Artifact getArtifact();

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
