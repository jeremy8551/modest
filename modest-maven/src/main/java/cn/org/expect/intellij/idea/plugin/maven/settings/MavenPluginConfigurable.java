package cn.org.expect.intellij.idea.plugin.maven.settings;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.Duration;
import javax.swing.*;

import cn.org.expect.expression.MillisExpression;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginApplication;
import cn.org.expect.intellij.idea.plugin.maven.impl.SimpleMavenPluginSettings;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.maven.MavenOption;
import cn.org.expect.maven.impl.SimpleArtifactOption;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.XMLUtils;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBSlider;
import com.intellij.ui.components.JBTextField;

public class MavenPluginConfigurable implements Configurable {

    /** 配置信息 */
    private final MavenPluginSettings settings;

    /** 未持久化的配置 */
    private final MavenPluginSettings active;

    /** UI组件 */
    private JBSlider inputIntervalTime;
    private JComboBox<MavenOption> repository;
    private JBCheckBox autoSwitchTab;
    private JBTextField tabIndex;
    private JBCheckBox tabVisible;
    private JBCheckBox searchInAllTab;
    private JBTextField expireTimeMillis;
    private JBLabel expireTimeMillisMemo;
    private JBTextField elementPriority;
    private JComboBox<MavenOption> downloadType;

    public MavenPluginConfigurable() {
        this.settings = MavenSearchPluginApplication.get().getBean(MavenPluginSettings.class);
        this.active = this.settings.copy(); // 返回一个副本
    }

    /**
     * 在 settings 界面中的名称
     *
     * @return 配置名称
     */
    public String getDisplayName() {
        return MavenMessage.get("maven.search.settings.display", this.settings.getName());
    }

    public JComponent createComponent() {
        String pluginName = this.settings.getName();
        String tabName = this.settings.getTabName();
        String allTabName = this.settings.getAllTabName();
        SimpleMavenPluginSettings def = new SimpleMavenPluginSettings();

        inputIntervalTime = new JBSlider(100, 2000); // 最小值 0，最大值 100
        inputIntervalTime.setMajorTickSpacing(200); // 主刻度间隔
        inputIntervalTime.setMinorTickSpacing(200);  // 次刻度间隔
        inputIntervalTime.setPaintTicks(true);     // 显示刻度
        inputIntervalTime.setPaintLabels(true);    // 显示标签
        inputIntervalTime.setPreferredSize(new Dimension(500, 50));
        inputIntervalTime.addChangeListener(e -> active.setInputIntervalTime(inputIntervalTime.getValue()));

        repository = new JComboBox<>(MavenSearchPluginApplication.get().getRepositoryOptions());
        repository.addActionListener(e -> active.setRepositoryId(((MavenOption) repository.getSelectedItem()).value()));

        autoSwitchTab = new JBCheckBox(MavenMessage.get("maven.search.settings.auto.select.tab", tabName));
        autoSwitchTab.addActionListener(e -> active.setAutoSwitchTab(autoSwitchTab.isSelected()));

        tabIndex = new JBTextField(10);
        tabIndex.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                active.setTabIndex(StringUtils.parseInt(tabIndex.getText(), def.getTabIndex()));
                tabIndex.setText(String.valueOf(active.getTabIndex()));
            }
        });

        tabVisible = new JBCheckBox(MavenMessage.get("maven.search.settings.display.self.tab", tabName));
        tabVisible.addActionListener(e -> active.setTabVisible(tabVisible.isSelected()));

        downloadType = new JComboBox<>(MavenSearchPluginApplication.get().getDownloaderOptions());
        downloadType.addActionListener(e -> active.setDownloadWay(((MavenOption) downloadType.getSelectedItem()).value()));

        searchInAllTab = new JBCheckBox(MavenMessage.get("maven.search.settings.select.tab", allTabName, pluginName));
        searchInAllTab.addActionListener(e -> active.setUseAllTab(searchInAllTab.isSelected()));

        expireTimeMillisMemo = new JBLabel("");
        expireTimeMillis = new JBTextField(10);
        expireTimeMillis.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                long timeMillis = def.getExpireTimeMillis();
                try {
                    timeMillis = new MillisExpression(expireTimeMillis.getText()).value();
                } catch (Throwable ignored) {
                }
                active.setExpireTimeMillis(timeMillis);
                expireTimeMillis.setText(String.valueOf(active.getExpireTimeMillis()));
                expireTimeMillisMemo.setText(formatMillis(timeMillis));
            }
        });

        elementPriority = new JBTextField(10);
        elementPriority.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                active.setNavigationPriority(StringUtils.parseInt(elementPriority.getText(), def.getNavigationPriority()));
                elementPriority.setText(String.valueOf(active.getNavigationPriority()));
            }
        });

        // swing UI
        JBPanel panel = new JBPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距
        gbc.fill = GridBagConstraints.NONE; // 水平填充
        gbc.weightx = 1.0; // 保持一致的列宽

        String intUnit = MavenMessage.get("maven.search.settings.unit.integer");

        this.addSeparator(panel, gbc, "maven.search.settings.group.tab");
        this.addRow(panel, gbc, true, tabVisible, "maven.search.settings.display.self.tab.description", tabName);
        this.addRow(panel, gbc, true, searchInAllTab, "maven.search.settings.select.tab.description", allTabName, pluginName);
        this.addRow(panel, gbc, true, autoSwitchTab, "maven.search.settings.auto.select.tab.description", tabName);
        this.addRow(panel, gbc, true, "maven.search.settings.tab.position", tabIndex, new JBLabel(intUnit), "maven.search.settings.tab.position.description", tabName);

        this.addRow(panel, gbc);
        this.addSeparator(panel, gbc, "maven.search.settings.group.search");
        this.addRow(panel, gbc, true, "maven.search.settings.tab.default.select.repository", repository, null, "maven.search.settings.tab.default.select.repository.description", tabName);
        this.addRow(panel, gbc, true, "maven.search.settings.debounce.time", inputIntervalTime, null, "maven.search.settings.debounce.time.description", pluginName);
        this.addRow(panel, gbc, true, "maven.search.settings.result.expire.time", expireTimeMillis, expireTimeMillisMemo, "maven.search.settings.result.expire.time.description", pluginName);
        this.addRow(panel, gbc, true, "maven.search.settings.result.priority", elementPriority, new JBLabel(intUnit), "maven.search.settings.result.priority.description", pluginName);

        this.addRow(panel, gbc);
        this.addSeparator(panel, gbc, "maven.search.settings.group.download");
        this.addRow(panel, gbc, true, "maven.search.settings.download.source", downloadType, null, "maven.search.settings.download.source.description");

        // 添加占位组件
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // 占满整行
        gbc.weighty = 1.0; // 将剩余垂直空间分配给此行
        gbc.fill = GridBagConstraints.BOTH; // 组件填充水平和垂直空间
        panel.add(new JBPanel(), gbc); // 添加空白面板作为占位

        this.autowired(this.active);
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
        inner.add(new JBLabel(MavenMessage.get(title) + "  "), config);

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

    private void addRow(JPanel panel, GridBagConstraints gbc, boolean tab, JComponent component, String description, Object... descriptionParams) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // 占1列
        gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(this.addTab(component, tab), gbc);

        // 注释说明
        String value = MavenMessage.toString(description, descriptionParams);
        if (StringUtils.isNotBlank(value)) {
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2; // 占1列
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距

            String text = "<html>" + ((component instanceof JBCheckBox) ? "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" : "") + XMLUtils.escape(value) + "</html>";
            JBLabel descriptionLabel = new JBLabel(text);
            descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.PLAIN, 10));
            descriptionLabel.setForeground(Color.GRAY);
            panel.add(this.addTab(descriptionLabel, tab), gbc);
        }
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, boolean tab, String title, JComponent component, JBLabel memo, String description, Object... descriptionParams) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1; // 占1列
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距

        String value = MavenMessage.get(title);
        int begin = value.indexOf('(');
        if (begin != -1 && value.indexOf(')', begin) != -1) {
            String name = value.substring(0, begin);
            String unit = value.substring(begin);
            value = "<html>" + name + "<font size='3'>" + unit + "</font></html>";
        }
        panel.add(this.addTab(new JBLabel(value), tab), gbc);

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
        String descriptionValue = MavenMessage.toString(description, descriptionParams);
        if (StringUtils.isNotBlank(descriptionValue)) {
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2; // 占1列
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距

            String text = "<html>" + XMLUtils.escape(descriptionValue) + "</html>";
            JBLabel descriptionLabel = new JBLabel(text);
            descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.PLAIN, 10));
            descriptionLabel.setForeground(Color.GRAY);
            panel.add(this.addTab(descriptionLabel, tab), gbc);
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
     * 将时间戳转为：hh:mm:ss
     *
     * @param millis 时间戳
     * @return 字符串
     */
    public String formatMillis(long millis) {
        Duration duration = Duration.ofMillis(millis); // 将毫秒数转换为 Duration
        long hours = duration.toHours(); // 提取小时、分钟、秒
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * 填充数据
     *
     * @param settings 配置信息
     */
    public void autowired(MavenPluginSettings settings) {
        inputIntervalTime.setValue((int) settings.getInputIntervalTime());
        autoSwitchTab.setSelected(settings.isAutoSwitchTab());
        tabIndex.setText(String.valueOf(settings.getTabIndex()));
        tabVisible.setSelected(settings.isTabVisible());
        searchInAllTab.setSelected(settings.isUseAllTab());
        expireTimeMillis.setText(String.valueOf(settings.getExpireTimeMillis()));
        expireTimeMillisMemo.setText(this.formatMillis(settings.getExpireTimeMillis()));
        elementPriority.setText(String.valueOf(settings.getNavigationPriority()));
        this.setSelectedOption(repository, new SimpleArtifactOption(settings.getRepositoryId()));
        this.setSelectedOption(downloadType, new SimpleArtifactOption(settings.getDownloadWay()));
    }

    public void setSelectedOption(JComboBox<MavenOption> comboBox, MavenOption selected) {
        ComboBoxModel<MavenOption> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            MavenOption element = model.getElementAt(i);
            if (element.equals(selected)) {
                comboBox.setSelectedItem(element);
                break;
            }
        }
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


