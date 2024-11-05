package cn.org.expect.intellijidea.plugin.maven;

import java.awt.*;
import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigation;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigationItem;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.util.TextWithIcon;
import com.intellij.util.ui.NamedColorUtil;
import com.intellij.util.ui.UIUtil;

public class MavenFinderListCellRenderer extends SearchEverywherePsiRenderer {

    private MavenFinder mavenFinder;

    public MavenFinderListCellRenderer(MavenFinderContributor contributor, MavenFinder mavenFinder) {
        super(contributor);
        this.mavenFinder = mavenFinder;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof MavenFinderNavigationItem) {
            MavenFinderNavigationItem item = (MavenFinderNavigationItem) value;

            this.removeAll();
            this.myRightComponentWidth = 0;

            ListCellRenderer<Object> leftRenderer = createLeftRenderer(list, value);
            Component result = leftRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            JLabel left = new JLabel("", null, SwingConstants.LEFT);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); // 设置布局为 FlowLayout
            left.add(Box.createVerticalGlue());
            left.add(result, 0);
            left.add(Box.createVerticalGlue());
            left.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
            left.setHorizontalTextPosition(SwingConstants.RIGHT);
            left.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
            left.setPreferredSize(new Dimension(200, left.getHeight()));
            this.add(left, BorderLayout.WEST);

            JLabel middle = new JLabel(StringUtils.left(Dates.format19(item.getArtifact().getTimestamp()), 16), null, SwingConstants.LEFT);
            middle.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            middle.setHorizontalTextPosition(SwingConstants.RIGHT);
            middle.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
            middle.setPreferredSize(new Dimension(200, middle.getHeight()));
            this.add(middle, BorderLayout.CENTER);

            TextWithIcon itemLocation = getItemLocation(item);
            JLabel right = new JLabel(itemLocation.getText(), itemLocation.getIcon(), SwingConstants.RIGHT);
            right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
            right.setHorizontalTextPosition(SwingConstants.LEFT);
            right.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
            right.setPreferredSize(new Dimension(200, right.getHeight()));
            this.add(right, BorderLayout.EAST);

            myRightComponentWidth = right.getPreferredSize().width;
            myRightComponentWidth += middle.getPreferredSize().width;

            Color color = isSelected ? UIUtil.getListSelectionBackground(true) : left.getBackground();
            this.setBackground(color);

//            System.out.println(item.getPresentableText() + ", " + result.getClass().getName() + ", " + left.getPreferredSize().width + ", " + middle.getPreferredSize().width + ", " + right.getPreferredSize().width);
            left.setBackground(color);
            right.setBackground(color);
            middle.setBackground(color);
            return this;
        }

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
