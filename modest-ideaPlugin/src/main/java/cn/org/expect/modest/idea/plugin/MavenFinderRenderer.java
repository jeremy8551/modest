package cn.org.expect.modest.idea.plugin;

import java.awt.*;
import javax.swing.*;

import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItem;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationList;
import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.util.TextWithIcon;

public class MavenFinderRenderer extends SearchEverywherePsiRenderer {

    public MavenFinderRenderer(MavenFinderContributor contributor) {
        super(contributor);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JListRenderer.INSTANCE.setList(list);
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    /**
     * 渲染搜索结果右侧的图标和文字
     *
     * @param value 记录
     * @return 图标与文字信息
     */
    public TextWithIcon getItemLocation(Object value) {
        if (value instanceof MavenFinderNavigationItem) {
            String repository = ((MavenFinderNavigationItem) value).getArtifact().getRepositoryUrl();
            return new TextWithIcon(repository, MavenFinderIcons.MAVEN_REPOSITORY_RIGHT);
        }

        if (value instanceof MavenFinderNavigationList) {
            MavenArtifact item = ((MavenFinderNavigationList) value).getArtifact();
            return new TextWithIcon(item.getArtifactId(), MavenFinderIcons.MAVEN_REPOSITORY_RIGHT);
        }

        return super.getItemLocation(value);
    }
}
