package cn.org.expect.intellijidea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import com.intellij.util.TextWithIcon;

/**
 * 查询结果导航结果中每条记录的接口
 */
public interface MavenFinderNavigation {

    /**
     * 设置导航记录左侧的图标
     *
     * @param icon 图标
     */
    void setIcon(Icon icon);

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
    TextWithIcon getRightLabel();
}
