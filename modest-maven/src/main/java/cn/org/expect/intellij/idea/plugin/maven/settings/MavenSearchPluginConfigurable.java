package cn.org.expect.intellij.idea.plugin.maven.settings;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

import cn.org.expect.expression.MillisExpression;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginApplication;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginSettings;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginUtils;
import cn.org.expect.maven.search.ArtifactOption;
import cn.org.expect.maven.search.ArtifactSearchMessage;
import cn.org.expect.maven.search.SimpleArtifactOption;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.XMLUtils;
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
    private JComboBox<ArtifactOption> repository;
    private JBCheckBox autoSwitchTab;
    private JBTextField tabIndex;
    private JBCheckBox tabVisible;
    private JBCheckBox searchInAllTab;
    private JBTextField expireTimeMillis;
    private JBLabel expireTimeMillisMemo;
    private JBTextField elementPriority;
    private JComboBox<ArtifactOption> downloadType;

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
        return ArtifactSearchMessage.get("maven.search.settings.display", this.settings.getName());
    }

    public JComponent createComponent() {
        String pluginName = this.settings.getName();
        String tabName = MavenSearchPluginUtils.getTabName();
        String allTabName = MavenSearchPluginUtils.getAllTabName();

        inputIntervalTime = new JBSlider(100, 2000); // 最小值 0，最大值 100
        inputIntervalTime.setMajorTickSpacing(200); // 主刻度间隔
        inputIntervalTime.setMinorTickSpacing(200);  // 次刻度间隔
        inputIntervalTime.setPaintTicks(true);     // 显示刻度
        inputIntervalTime.setPaintLabels(true);    // 显示标签
        inputIntervalTime.setPreferredSize(new Dimension(500, 50));
        inputIntervalTime.addChangeListener(e -> active.setInputIntervalTime(inputIntervalTime.getValue()));

        repository = new JComboBox<>(MavenSearchPluginApplication.get().getRepositoryOptions());
        repository.addActionListener(e -> active.setRepositoryId(((ArtifactOption) repository.getSelectedItem()).getKey()));

        autoSwitchTab = new JBCheckBox(ArtifactSearchMessage.get("maven.search.settings.auto.select.tab", tabName));
        autoSwitchTab.addActionListener(e -> active.setAutoSwitchTab(autoSwitchTab.isSelected()));

        tabIndex = new JBTextField(10);
        tabIndex.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                active.setTabIndex(StringUtils.parseInt(tabIndex.getText(), MavenSearchPluginSettings.DEFAULT_TAB_INDEX));
                tabIndex.setText(String.valueOf(active.getTabIndex()));
            }
        });

        tabVisible = new JBCheckBox(ArtifactSearchMessage.get("maven.search.settings.select.tab", tabName, pluginName));
        tabVisible.addActionListener(e -> active.setTabVisible(tabVisible.isSelected()));

        downloadType = new JComboBox<>(MavenSearchPluginApplication.get().getDownloaderOptions());
        downloadType.addActionListener(e -> active.setDownloadWay(((ArtifactOption) downloadType.getSelectedItem()).getKey()));

        searchInAllTab = new JBCheckBox(ArtifactSearchMessage.get("maven.search.settings.select.tab", allTabName, pluginName));
        searchInAllTab.addActionListener(e -> active.setUseAllTab(searchInAllTab.isSelected()));

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

        // swing UI
        JBPanel panel = new JBPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0); // 设置每个组件的间距
        gbc.fill = GridBagConstraints.NONE; // 水平填充
        gbc.weightx = 1.0; // 保持一致的列宽

        String intUnit = ArtifactSearchMessage.get("maven.search.settings.unit.integer");

        this.addSeparator(panel, gbc, ArtifactSearchMessage.get("maven.search.settings.group.tab"));
        this.addRow(panel, gbc, true, tabVisible, ArtifactSearchMessage.get("maven.search.settings.select.tab.description", tabName));
        this.addRow(panel, gbc, true, searchInAllTab, ArtifactSearchMessage.get("maven.search.settings.select.tab.description", allTabName));
        this.addRow(panel, gbc, true, autoSwitchTab, ArtifactSearchMessage.get("maven.search.settings.auto.select.tab.description", tabName));
        this.addRow(panel, gbc, true, ArtifactSearchMessage.get("maven.search.settings.tab.position"), tabIndex, new JBLabel(intUnit), ArtifactSearchMessage.get("maven.search.settings.tab.position.description", tabName));

        this.addRow(panel, gbc);
        this.addSeparator(panel, gbc, ArtifactSearchMessage.get("maven.search.settings.group.search"));
        this.addRow(panel, gbc, true, ArtifactSearchMessage.get("maven.search.settings.tab.default.select.repository"), repository, null, ArtifactSearchMessage.get("maven.search.settings.tab.default.select.repository.description", tabName));
        this.addRow(panel, gbc, true, ArtifactSearchMessage.get("maven.search.settings.debounce.time"), inputIntervalTime, null, ArtifactSearchMessage.get("maven.search.settings.debounce.time.description", pluginName));
        this.addRow(panel, gbc, true, ArtifactSearchMessage.get("maven.search.settings.result.expire.time"), expireTimeMillis, expireTimeMillisMemo, ArtifactSearchMessage.get("maven.search.settings.result.expire.time.description", pluginName));
        this.addRow(panel, gbc, true, ArtifactSearchMessage.get("maven.search.settings.result.priority"), elementPriority, new JBLabel(intUnit), ArtifactSearchMessage.get("maven.search.settings.result.priority.description", pluginName));

        this.addRow(panel, gbc);
        this.addSeparator(panel, gbc, ArtifactSearchMessage.get("maven.search.settings.group.download"));
        this.addRow(panel, gbc, true, ArtifactSearchMessage.get("maven.search.settings.download.source"), downloadType, null, ArtifactSearchMessage.get("maven.search.settings.download.source.description"));

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
        autoSwitchTab.setSelected(settings.isAutoSwitchTab());
        tabIndex.setText(String.valueOf(settings.getTabIndex()));
        tabVisible.setSelected(settings.isTabVisible());
        searchInAllTab.setSelected(settings.isUseAllTab());
        expireTimeMillis.setText(String.valueOf(settings.getExpireTimeMillis()));
        expireTimeMillisMemo.setText(MavenSearchPluginUtils.format(settings.getExpireTimeMillis()));
        elementPriority.setText(String.valueOf(settings.getElementPriority()));
        this.setSelectedOption(repository, new SimpleArtifactOption(settings.getRepositoryId()));
        this.setSelectedOption(downloadType, new SimpleArtifactOption(settings.getDownloadWay()));
    }

    public void setSelectedOption(JComboBox<ArtifactOption> comboBox, ArtifactOption selected) {
        ComboBoxModel<ArtifactOption> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ArtifactOption element = model.getElementAt(i);
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


