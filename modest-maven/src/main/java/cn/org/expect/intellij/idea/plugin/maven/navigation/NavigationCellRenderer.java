package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.awt.*;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContributor;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.TextWithIcon;
import com.intellij.util.ui.NamedColorUtil;
import com.intellij.util.ui.UIUtil;

public class NavigationCellRenderer extends SearchEverywherePsiRenderer {

    public NavigationCellRenderer(MavenSearchPluginContributor contributor) {
        super(contributor);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof SearchNavigationItem) {
            SearchNavigationItem item = (SearchNavigationItem) value;
            TextWithIcon itemLocation = item.getRightIcon();

            this.removeAll();

            String leftText = StringUtils.trimBlank(item.getPresentableText());
            String middleText = StringUtils.left(Dates.format19(item.getArtifact().getTimestamp()), 16);

            Component leftComponent = new NavigationCell(leftText, SimpleTextAttributes.STYLE_PLAIN, JBColor.BLACK).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Component middleComponent = new NavigationCell(middleText, SimpleTextAttributes.STYLE_SMALLER, JBColor.GRAY).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            JPanel left = new JPanel();
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); // 设置布局为 FlowLayout
            left.add(Box.createVerticalGlue());
            left.add(leftComponent, 0);
            left.add(Box.createVerticalGlue());
            left.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
            left.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
            left.setPreferredSize(new Dimension(150, left.getHeight()));
            this.add(left, BorderLayout.WEST);

            JPanel middle = new JPanel();
            middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS)); // 设置布局为 FlowLayout
            middle.add(Box.createVerticalGlue());
            middle.add(middleComponent, 0);
            middle.add(Box.createVerticalGlue());
            middle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
            middle.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
            middle.setPreferredSize(new Dimension(150, middle.getHeight()));
            this.add(middle, BorderLayout.CENTER);

            JLabel right = new JLabel(itemLocation.getText(), itemLocation.getIcon(), SwingConstants.RIGHT);
            right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
            right.setHorizontalTextPosition(SwingConstants.LEFT);
            right.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
            right.setPreferredSize(new Dimension(200, right.getHeight()));
            this.add(right, BorderLayout.EAST);

            this.myRightComponentWidth = right.getPreferredSize().width;
            this.myRightComponentWidth += middle.getPreferredSize().width;

            Color color = isSelected ? UIUtil.getListSelectionBackground(true) : left.getBackground();
            this.setBackground(color);
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
        if (value instanceof MavenSearchNavigation) {
            MavenSearchNavigation navigation = (MavenSearchNavigation) value;
            return navigation.getRightIcon();
        } else {
            return super.getItemLocation(value);
        }
    }
}
