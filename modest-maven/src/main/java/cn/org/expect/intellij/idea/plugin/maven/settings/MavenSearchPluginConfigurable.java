package cn.org.expect.intellij.idea.plugin.maven.settings;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import javax.swing.*;

import cn.org.expect.expression.MillisExpression;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginApplication;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginSettings;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginUtils;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchScopeDescriptor;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.XMLUtils;
import com.intellij.ide.util.scopeChooser.ScopeDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBSlider;
import com.intellij.ui.components.JBTextField;

public class MavenSearchPluginConfigurable implements Configurable {

    /** 配置信息 */
    private final MavenSearchPluginSettings settings;

    /** 未持久化的配置 */
    private final MavenSearchPluginSettings active;

    /** UI组件 */
    private JBSlider inputIntervalTime;
    private JComboBox<String> repositoryId;
    private JBCheckBox autoSwitchTab;
    private JBTextField tabIndex;
    private JBCheckBox tabVisible;
    private JBCheckBox searchInAllTab;
    private JBTextField expireTimeMillis;
    private JBLabel expireTimeMillisMemo;
    private JBTextField elementPriority;

    public MavenSearchPluginConfigurable() {
        this.settings = MavenSearchPluginApplication.get().getBean(MavenSearchPluginSettings.class);
        this.active = this.settings.copy(); // 返回一个副本
    }

    /**
     * 在 settings 界面中的名称
     *
     * @return 配置名称
     */
    public String getDisplayName() {
        return this.settings.getName() + " Settings";
    }

    public JComponent createComponent() {
        String name = this.settings.getName();
        String tabName = MavenSearchPluginUtils.getTabName();

        inputIntervalTime = new JBSlider(100, 2000); // 最小值 0，最大值 100
        inputIntervalTime.setMajorTickSpacing(200); // 主刻度间隔
        inputIntervalTime.setMinorTickSpacing(200);  // 次刻度间隔
        inputIntervalTime.setPaintTicks(true);     // 显示刻度
        inputIntervalTime.setPaintLabels(true);    // 显示标签
        inputIntervalTime.setPreferredSize(new Dimension(500, 50));
        inputIntervalTime.addChangeListener(e -> active.setInputIntervalTime(inputIntervalTime.getValue()));

        // 默认 Maven 仓库
        List<MavenSearchScopeDescriptor> scopeList = MavenSearchScopeDescriptor.getList();
        String[] repositoryIdItems = scopeList.stream().map(ScopeDescriptor::getDisplayName).toArray(String[]::new);
        repositoryId = new JComboBox<>(repositoryIdItems);
        repositoryId.addActionListener(e -> active.setRepositoryId(MavenSearchScopeDescriptor.toRepositoryId((String) repositoryId.getSelectedItem())));

        autoSwitchTab = new JBCheckBox("自动切换到 " + tabName + " 选项卡");
        autoSwitchTab.addActionListener(e -> active.setAutoSwitchTab(autoSwitchTab.isSelected()));

        tabIndex = new JBTextField(10);
        tabIndex.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                active.setTabIndex(StringUtils.parseInt(tabIndex.getText(), MavenSearchPluginSettings.DEFAULT_TAB_INDEX));
                tabIndex.setText(String.valueOf(active.getTabIndex()));
            }
        });

        tabVisible = new JBCheckBox("使用 " + tabName + " 选项卡执行 " + name + " 搜索功能");
        tabVisible.addActionListener(e -> active.setTabVisible(tabVisible.isSelected()));

        searchInAllTab = new JBCheckBox("使用 All 选项卡执行 " + name + " 搜索功能");
        searchInAllTab.addActionListener(e -> active.setSearchInAllTab(searchInAllTab.isSelected()));

        expireTimeMillisMemo = new JBLabel("");
        expireTimeMillis = new JBTextField(10);
        expireTimeMillis.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                long timeMillis = MavenSearchPluginSettings.DEFAULT_EXPIRE_TIME_MILLIS;
                try {
                    timeMillis = new MillisExpression(expireTimeMillis.getText()).value();
                } catch (Throwable ignored) {
                }
                active.setExpireTimeMillis(timeMillis);
                expireTimeMillis.setText(String.valueOf(active.getExpireTimeMillis()));
                expireTimeMillisMemo.setText(MavenSearchPluginUtils.format(timeMillis));
            }
        });

        elementPriority = new JBTextField(10);
        elementPriority.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                active.setElementPriority(StringUtils.parseInt(elementPriority.getText(), MavenSearchPluginSettings.DEFAULT_ELEMENT_PRIORITY));
                elementPriority.setText(String.valueOf(active.getElementPriority()));
            }
        });

        this.autowired(this.active);

        // swing UI
        JBPanel panel = new JBPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距
        gbc.fill = GridBagConstraints.NONE; // 水平填充
        gbc.weightx = 1.0; // 保持一致的列宽

        this.addSeparator(panel, gbc, "Tab 设置");
        this.addRow(panel, gbc, true, tabVisible, "双击 shift 弹出 SearchEverywhere 界面，使用 " + tabName + " 选项卡搜索 Maven 仓库.");
        this.addRow(panel, gbc, true, searchInAllTab, "双击 shift 弹出 SearchEverywhere 界面，可以使用 All 选项卡搜索 Maven 仓库.");
        this.addRow(panel, gbc, true, autoSwitchTab, "选中 pom.xml 中的依赖信息，双击 shift 自动切换 " + tabName + " 选项卡执行搜索.");
        this.addRow(panel, gbc, true, "位置索引", tabIndex, new JBLabel("整数"), "双击 shift 弹出 SearchEverywhere 界面，" + tabName + " 选项卡所在位置，0 表示标签页面板的第一个位置");

        this.addRow(panel, gbc);
        this.addSeparator(panel, gbc, "搜索设置");
        this.addRow(panel, gbc, true, "默认搜索", repositoryId, null, tabName + " 选项卡默认使用的 Maven 仓库.");
        this.addRow(panel, gbc, true, "防抖时间(毫秒)", inputIntervalTime, null, "双击 shift 弹出 SearchEverywhere 界面，在搜索框中输入文本，在一定时间内没有变化才会执行 " + name + " 搜索.");
        this.addRow(panel, gbc, true, "超时时间(毫秒)", expireTimeMillis, expireTimeMillisMemo, name + " 搜索结果保留的最长时间，支持表达式：24*3600*1000");
        this.addRow(panel, gbc, true, "排序权重", elementPriority, new JBLabel("整数"), "SearchEverywhere 对搜索结果排序时，" + name + " 搜索结果的权重.");

        // 添加占位组件
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // 占满整行
        gbc.weighty = 1.0; // 将剩余垂直空间分配给此行
        gbc.fill = GridBagConstraints.BOTH; // 组件填充水平和垂直空间
        panel.add(new JBPanel(), gbc); // 添加空白面板作为占位
        return panel;
    }

    public void addSeparator(JBPanel panel, GridBagConstraints gbc, String title) {
        JBPanel inner = new JBPanel();
        inner.setLayout(new GridBagLayout());

        GridBagConstraints config = new GridBagConstraints();
        config.insets = new Insets(0, 0, 0, 0);
        config.fill = GridBagConstraints.HORIZONTAL;

        // 左侧标题
        config.gridx = 0;
        config.weightx = 0; // 固定宽度
        inner.add(new JBLabel(title + "  "), config);

        // 右侧分隔线
        config.gridx = 1;
        config.weightx = 1.0; // 占据剩余空间
        inner.add(new JSeparator(), config);

        gbc.gridx = 0; // 第一列
        gbc.gridwidth = 2; // 占2列
        gbc.gridy++; // 换行
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 0, 7, 0); // 设置每个组件的间距
        panel.add(inner, gbc);
    }

    /**
     * 添加空行
     */
    public void addRow(JBPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0; // 第一列
        gbc.gridwidth = 2; // 占2列
        gbc.gridy++; // 换行
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0); // 设置每个组件的间距
        panel.add(new JBLabel(""), gbc);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, boolean tab, JComponent component, String... array) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // 占1列
        gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(this.addTab(component, tab), gbc);

        // 注释说明
        for (String description : array) {
            if (StringUtils.isNotBlank(description)) {
                gbc.gridx = 0;
                gbc.gridy++;
                gbc.gridwidth = 2; // 占1列
                gbc.fill = GridBagConstraints.NONE;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距

                String text = "<html>" + ((component instanceof JBCheckBox) ? "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" : "") + XMLUtils.escape(description) + "</html>";
                JBLabel descriptionLabel = new JBLabel(text);
                descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.PLAIN, 10));
                descriptionLabel.setForeground(Color.GRAY);
                panel.add(this.addTab(descriptionLabel, tab), gbc);
            }
        }
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, boolean tab, String title, JComponent component, JBLabel memo, String... array) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1; // 占1列
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距

        int begin = title.indexOf('(');
        if (begin != -1 && title.indexOf(')', begin) != -1) {
            String name = title.substring(0, begin);
            String unit = title.substring(begin);
            title = "<html>" + name + "<font size='3'>" + unit + "</font></html>";
        }
        panel.add(this.addTab(new JBLabel(title), tab), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;

        if (memo == null) {
            panel.add(component, gbc);
        } else {
            JBPanel inner = new JBPanel();
            inner.add(component);
            inner.add(memo);
            panel.add(inner, gbc);
        }

        // 注释说明
        for (String description : array) {
            if (StringUtils.isNotBlank(description)) {
                gbc.gridx = 0;
                gbc.gridy++;
                gbc.gridwidth = 2; // 占1列
                gbc.fill = GridBagConstraints.NONE;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距

                String text = "<html>" + XMLUtils.escape(description) + "</html>";
                JBLabel descriptionLabel = new JBLabel(text);
                descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.PLAIN, 10));
                descriptionLabel.setForeground(Color.GRAY);
                panel.add(this.addTab(descriptionLabel, tab), gbc);
            }
        }
    }

    public JComponent addTab(JComponent component, boolean tab) {
        if (tab) {
            JBPanel panel = new JBPanel();
            panel.add(new JBLabel("  "));
            panel.add(component);
            return panel;
        } else {
            return component;
        }
    }

    /**
     * 填充数据
     *
     * @param settings 配置信息
     */
    public void autowired(MavenSearchPluginSettings settings) {
        inputIntervalTime.setValue((int) settings.getInputIntervalTime());
        repositoryId.setSelectedItem(MavenSearchScopeDescriptor.getList().stream().filter(s -> s.getScope().getRepositoryId().equals(settings.getRepositoryId())).findAny().get().getDisplayName());
        autoSwitchTab.setSelected(settings.isAutoSwitchTab());
        tabIndex.setText(String.valueOf(settings.getTabIndex()));
        tabVisible.setSelected(settings.isTabVisible());
        searchInAllTab.setSelected(settings.isSearchInAllTab());
        expireTimeMillis.setText(String.valueOf(settings.getExpireTimeMillis()));
        expireTimeMillisMemo.setText(MavenSearchPluginUtils.format(settings.getExpireTimeMillis()));
        elementPriority.setText(String.valueOf(settings.getElementPriority()));
    }

    /**
     * 检查是否有修改
     *
     * @return 返回true表示有变化
     */
    public boolean isModified() {
        return !this.settings.isEquals(this.active);
    }

    /**
     * 将 Swing 表单中的设置存储到可配置组件中。此方法应用户请求在 EDT 上调用。
     */
    public void apply() {
        this.settings.merge(this.active).save();
    }

    /**
     * 将设置加载到 Swing 表单中。<br>
     * 此方法在表单创建后立即在 EDT 上调用，稍后应用户请求调用。
     */
    public void reset() {
        this.autowired(this.settings);
    }
}


