package cn.org.expect.intellijidea.plugin.maven;

import java.awt.*;
import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigation;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigationItem;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.ide.util.PSIRenderingUtils;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.IconUtil;
import com.intellij.util.TextWithIcon;
import com.intellij.util.ui.NamedColorUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

public class MavenFinderListCellRenderer extends SearchEverywherePsiRenderer {

    private MavenFinder mavenFinder;

    public MavenFinderListCellRenderer(MavenFinderContributor contributor, MavenFinder mavenFinder) {
        super(contributor);
        this.mavenFinder = mavenFinder;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof MavenFinderNavigationItem) {
            MavenFinderNavigationItem item = (MavenFinderNavigationItem) value;
            TextWithIcon itemLocation = item.getRightLabel();

            this.removeAll();

            String leftText = StringUtils.trimBlank(item.getPresentableText());
            String middleText = StringUtils.left(Dates.format19(item.getArtifact().getTimestamp()), 16);

            Component leftComponent = new CellRenderer(leftText, SimpleTextAttributes.STYLE_PLAIN, JBColor.BLACK).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Component middleComponent = new CellRenderer(middleText, SimpleTextAttributes.STYLE_SMALLER, JBColor.GRAY).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

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

//            System.out.println(item.getPresentableText() + ", " + result.getClass().getName() + ", " + left.getPreferredSize().width + ", " + middle.getPreferredSize().width + ", " + right.getPreferredSize().width);
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
        if (value instanceof MavenFinderNavigation) {
            MavenFinderNavigation navigation = (MavenFinderNavigation) value;
            return navigation.getRightLabel();
        } else {
            return super.getItemLocation(value);
        }
    }

    public static class CellRenderer extends ColoredListCellRenderer<Object> {

        private String text;

        private Icon icon;

        private int style;

        private Color fgColor;

        public CellRenderer(String text, int style, Color fgColor) {
            this.text = text;
            this.icon = null;
            this.style = style;
            this.fgColor = fgColor;
        }

        public CellRenderer(String text, int style, Color fgColor, Icon icon) {
            this.text = text;
            this.icon = icon;
            this.style = style;
            this.fgColor = fgColor;
        }

        @Override
        protected void customizeCellRenderer(@NotNull JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
            SimpleTextAttributes simple = null;

            TextAttributes attributes = PSIRenderingUtils.getNavigationItemAttributesStatic(value);
            if (attributes != null) {
                simple = SimpleTextAttributes.fromTextAttributes(attributes);
            }

            if (simple == null) {
            }

            // this.append(locationString, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
            this.append(this.text, new SimpleTextAttributes(this.style, this.fgColor));
            this.setIcon(this.icon == null ? IconUtil.getEmptyIcon(false) : this.icon);

            Color bgColor = UIUtil.getListBackground();
            this.setBackground(selected ? UIUtil.getListSelectionBackground(true) : bgColor);
        }
    }
}
