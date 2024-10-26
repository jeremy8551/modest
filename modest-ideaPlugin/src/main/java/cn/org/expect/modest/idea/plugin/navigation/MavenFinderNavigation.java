package cn.org.expect.modest.idea.plugin.navigation;

import javax.swing.*;

/**
 * 查询结果导航结果中每条记录的接口
 */
public interface MavenFinderNavigation {

    void setIcon(Icon icon);

    MavenArtifact getArtifact();
}
