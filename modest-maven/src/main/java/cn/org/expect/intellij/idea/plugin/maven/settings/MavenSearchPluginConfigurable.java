package cn.org.expect.intellij.idea.plugin.maven.settings;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginApplication;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginSettings;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchScopeDescriptor;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.util.scopeChooser.ScopeDescriptor;
import com.intellij.openapi.options.Configurable;

public class MavenSearchPluginConfigurable implements Configurable {

    /** 配置信息 */
    private final MavenSearchPluginSettings settings;

    /** 未持久化的配置 */
    private final MavenSearchPluginSettings active;

    /** UI组件 */
    private JSlider inputIntervalTime;
    private JComboBox<String> repositoryId;
    private JCheckBox autoSwitchTab;
    private JTextField tabIndex;
    private JCheckBox tabVisible;
    private JCheckBox searchInAllTab;
    private JTextField expireTimeMillis;
    private JTextField elementPriority;

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
        inputIntervalTime = new JSlider(100, 2000); // 最小值 0，最大值 100
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

        autoSwitchTab = new JCheckBox("选中 <groupId>gid</groupId><artifactId>aid</artifactId> 自动切换到标签页进行查询");
        autoSwitchTab.addActionListener(e -> active.setAutoSwitchTab(autoSwitchTab.isSelected()));

        tabIndex = new JTextField(10);
        tabIndex.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                active.setTabIndex(StringUtils.parseInt(tabIndex.getText(), MavenSearchPluginSettings.DEFAULT_TAB_INDEX));
                tabIndex.setText(String.valueOf(active.getTabIndex()));
            }
        });

        tabVisible = new JCheckBox("");
        tabVisible.addActionListener(e -> active.setTabVisible(tabVisible.isSelected()));

        searchInAllTab = new JCheckBox("是否支持在 All 标签页中搜索");
        searchInAllTab.addActionListener(e -> active.setSearchInAllTab(searchInAllTab.isSelected()));

        expireTimeMillis = new JTextField(10);
        expireTimeMillis.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                active.setExpireTimeMillis(StringUtils.parseInt(expireTimeMillis.getText(), MavenSearchPluginSettings.DEFAULT_EXPIRE_TIME_MILLIS));
                expireTimeMillis.setText(String.valueOf(active.getExpireTimeMillis()));
            }
        });

        elementPriority = new JTextField(10);
        elementPriority.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                active.setElementPriority(StringUtils.parseInt(elementPriority.getText(), MavenSearchPluginSettings.DEFAULT_ELEMENT_PRIORITY));
                elementPriority.setText(String.valueOf(active.getElementPriority()));
            }
        });

        this.autowired(this.active);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 垂直布局
        addRow(panel, "Tab 显示:", tabVisible);
        addRow(panel, "Tab 切换:", autoSwitchTab);
        addRow(panel, "Tab 位置:", tabIndex);
        addRow(panel, "Tab All:", searchInAllTab);
        addRow(panel, "Maven仓库:", repositoryId);
        addRow(panel, "输入间隔:", inputIntervalTime);
        addRow(panel, "超时时间", expireTimeMillis);
        addRow(panel, "排序权重", elementPriority);
        return panel;
    }

    private static void addRow(JPanel panel, String title, JComponent component) {
        JLabel label = new JLabel(title);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setPreferredSize(new Dimension(100, 50));

        JPanel label1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        label1.add(label);
        label1.add(component);

        panel.add(label1);
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


