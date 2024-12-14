package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContributor;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.TextWithIcon;
import com.intellij.util.ui.NamedColorUtil;
import com.intellij.util.ui.UIUtil;

public class MavenSearchNavigationRenderer extends SearchEverywherePsiRenderer {
    private final static Log log = LogFactory.getLog(MavenSearchNavigationRenderer.class);

    public final static int LEFT_CELL_WITH = 150;

    public final static int RIGHT_CELL_WITH = 200;

    private final MavenSearchPluginContributor contributor;

    public MavenSearchNavigationRenderer(MavenSearchPluginContributor contributor) {
        super(contributor);
        this.contributor = Ensure.notNull(contributor);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // 其他搜索类别的导航记录
        if (!(value instanceof MavenSearchNavigation)) {
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }

        // 导航栏
        MavenSearchNavigation navigation = (MavenSearchNavigation) value;

        // 第一层
        if (navigation.getDepth() == 1) {
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }

        // 第二层
        if (navigation.getDepth() == 2) {
            return this.renderItem(list, value, index, isSelected, cellHasFocus, navigation);
        }

        // 第三层
        if (navigation.getDepth() == 3) {
            return this.renderDetail(list, value, index, isSelected, cellHasFocus, navigation);
        }

        // 其他
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    private Component renderItem(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus, MavenSearchNavigation navigation) {
        this.removeAll();

        Icon leftIcon = navigation.getLeftIcon();
        String leftText = StringUtils.trimBlank(navigation.getPresentableText());
        Date timestamp = navigation.getArtifact().getTimestamp();
        String middleText = timestamp == null ? "" : StringUtils.left(Dates.format19(timestamp), 16);
        String rightText = this.parseJDKVersion(contributor.getPlugin().getLocalRepository().getJarfile(navigation.getArtifact()));
        Icon rightIcon = navigation.getRightIcon();

        Component leftComponent = new NavigationCell(leftIcon, leftText, SimpleTextAttributes.STYLE_PLAIN, JBColor.BLACK).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Component middleComponent = new NavigationCell(null, middleText, SimpleTextAttributes.STYLE_SMALLER, JBColor.GRAY).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        middleComponent.setPreferredSize(new Dimension(100, middleComponent.getHeight()));

        // 左侧的文本，无图标
        JBPanel left = new JBPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); // 设置布局为 FlowLayout
        left.add(leftComponent);
        left.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
        left.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
        left.setPreferredSize(new Dimension(MavenSearchNavigationRenderer.LEFT_CELL_WITH, left.getHeight()));
        this.add(left, BorderLayout.WEST);

        // 中间文本，无图标
        JBPanel middle = new JBPanel();
        middle.setLayout(new BorderLayout());
        middle.add(middleComponent, BorderLayout.WEST);
        middle.add(new JBLabel(""), BorderLayout.CENTER);
        middle.add(new JBLabel(""), BorderLayout.EAST);
        middle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
        middle.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
        this.add(middle, BorderLayout.CENTER);

        // 右侧的图标与文本
        JBLabel right = new JBLabel(rightText, rightIcon, SwingConstants.RIGHT);
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
        right.setHorizontalTextPosition(SwingConstants.LEFT);
        right.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
        right.setPreferredSize(new Dimension(MavenSearchNavigationRenderer.RIGHT_CELL_WITH, right.getHeight()));
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

    private Component renderDetail(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus, MavenSearchNavigation navigation) {
        this.removeAll();

        String leftText = navigation.getPresentableText();
        String middleText = navigation.getLocationString();
        String rightText = navigation.getRightText();
        Icon rightIcon = navigation.getRightIcon();

        Component leftComponent = new NavigationCell(null, leftText, SimpleTextAttributes.STYLE_PLAIN, JBColor.BLACK).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Component middleComponent = new NavigationCell(null, middleText, SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // 左侧的文本，无图标
        JBPanel left = new JBPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); // 设置布局为 FlowLayout
        left.add(leftComponent);
        left.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
        left.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
        left.setPreferredSize(new Dimension(MavenSearchNavigationRenderer.LEFT_CELL_WITH, left.getHeight()));
        this.add(left, BorderLayout.WEST);

        // 中间文本，无图标
        JBPanel middle = new JBPanel();
        middle.setLayout(new BorderLayout());
        middle.add(middleComponent, BorderLayout.WEST);
        middle.add(new JBLabel(""), BorderLayout.CENTER);
        middle.add(new JBLabel(""), BorderLayout.EAST);
        middle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
        middle.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
        this.add(middle, BorderLayout.CENTER);

        // 右侧的图标与文本
        JBLabel right = new JBLabel(rightText, rightIcon, SwingConstants.RIGHT);
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.getListCellHPadding()));
        right.setHorizontalTextPosition(SwingConstants.LEFT);
        right.setForeground(isSelected ? NamedColorUtil.getListSelectionForeground(true) : NamedColorUtil.getInactiveTextColor());
        right.setPreferredSize(new Dimension(MavenSearchNavigationRenderer.RIGHT_CELL_WITH, right.getHeight()));
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

    /**
     * 渲染搜索结果右侧的图标和文字
     *
     * @param value 记录
     * @return 图标与文字信息
     */
    public TextWithIcon getItemLocation(Object value) {
        if (value instanceof MavenSearchNavigation) {
            MavenSearchNavigation navigation = (MavenSearchNavigation) value;
            return new TextWithIcon(navigation.getRightText(), navigation.getRightIcon());
        } else {
            return super.getItemLocation(value);
        }
    }

    protected String parseJDKVersion(File file) {
        if (file != null && file.exists() && file.isFile() && file.length() > 0) {
            JarFile jarfile = null;
            try {
                jarfile = new JarFile(file);
                JarEntry entry = jarfile.stream().filter(e -> e.getName().endsWith(".class")).findFirst().orElse(null);
                if (entry != null) {
                    String prefix = "Java ";
                    try (InputStream in = jarfile.getInputStream(entry)) {
                        in.skip(6); // Skip the first 6 bytes
                        int major = in.read() << 8 | in.read();
                        switch (major) {
                            case 45:
                                return prefix + "1.1";
                            case 46:
                                return prefix + "1.2";
                            case 47:
                                return prefix + "1.3";
                            case 48:
                                return prefix + "1.4";
                            case 49:
                                return prefix + "5";
                            case 50:
                                return prefix + "6";
                            case 51:
                                return prefix + "7";
                            case 52:
                                return prefix + "8";
                            case 53:
                                return prefix + "9";
                            case 54:
                                return prefix + "10";
                            case 55:
                                return prefix + "11";
                            case 56:
                                return prefix + "12";
                            case 57:
                                return prefix + "13";
                            case 58:
                                return prefix + "14";
                            case 59:
                                return prefix + "15";
                            case 60:
                                return prefix + "16";
                            case 61:
                                return prefix + "17";
                            case 62:
                                return prefix + "18";
                            case 63:
                                return prefix + "19";
                            case 64:
                                return prefix + "20";
                            case 65:
                                return prefix + "21";
                            case 66:
                                return prefix + "22";
                        }
                    }
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage());
            } finally {
                IO.closeQuietly(jarfile);
            }
        }
        return "";
    }
}
