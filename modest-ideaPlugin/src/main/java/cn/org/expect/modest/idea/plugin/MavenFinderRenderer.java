package cn.org.expect.modest.idea.plugin;

import java.awt.*;
import javax.swing.*;

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
    protected TextWithIcon getItemLocation(Object value) {
        if (value instanceof MavenFinderNavigationItem) {
            String repository = ((MavenFinderNavigationItem) value).getPresentation().getItem().getRepositoryUrl();
            return new TextWithIcon(repository, Icons.MAVEN_REPOSITORY_RIGHT);
        } else {
            return super.getItemLocation(value);
        }
    }
}
