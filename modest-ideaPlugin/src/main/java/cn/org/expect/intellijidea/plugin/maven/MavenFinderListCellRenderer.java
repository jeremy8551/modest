package cn.org.expect.intellijidea.plugin.maven;

import java.awt.*;
import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigation;
import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.util.TextWithIcon;

public class MavenFinderListCellRenderer extends SearchEverywherePsiRenderer {

    private MavenFinder mavenFinder;

    public MavenFinderListCellRenderer(MavenFinderContributor contributor, MavenFinder mavenFinder) {
        super(contributor);
        this.mavenFinder = mavenFinder;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    /**
     * 渲染搜索结果右侧的图标和文字
     *
     * @param value 记录
     * @return 图标与文字信息
     */
    public TextWithIcon getItemLocation(Object value) {
        if (value instanceof MavenFinderNavigation) {
            MavenFinderNavigation navigation = (MavenFinderNavigation) value;
            return navigation.getRightLabel();
        } else {
            return super.getItemLocation(value);
        }
    }
}
