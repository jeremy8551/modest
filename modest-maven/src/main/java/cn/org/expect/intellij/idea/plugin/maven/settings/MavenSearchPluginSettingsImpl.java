package cn.org.expect.intellij.idea.plugin.maven.settings;

import java.io.File;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginSettings;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.central.CentralMavenRepository;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;

/**
 * 插件配置信息
 */
public class MavenSearchPluginSettingsImpl implements MavenSearchPluginSettings {
    private final static Log log = LogFactory.getLog(MavenSearchPluginSettingsImpl.class);

    /** 插件ID */
    private String id;

    /** 插件名 */
    private String name;

    /** 插件工作目录 */
    private File workHome;

    /** 连续输入文本的间隔时间 */
    private volatile long inputIntervalTime;

    /** Maven 仓库ID，就是 {@linkplain EasyBean#value()} */
    private volatile String repositoryId;

    /** 如果选中文本是 groupId:artifactId:version 时，是否自动切换tab */
    private volatile boolean autoSwitchTab;

    /** 标签页所在的位置，从0开始 */
    private volatile int tabIndex;

    /** true 表示显示标签页 */
    private volatile boolean tabVisible;

    /** 查询结果的排序权重 */
    private volatile int elementPriority;

    /** 失效时间（单位毫秒） */
    private volatile long expireTimeMillis;

    /** true表示支持在 All 标签页中执行查询操作 */
    private volatile boolean searchInAllTab;

    public MavenSearchPluginSettingsImpl() {
        this.workHome = new File(Settings.getUserHome(), ".maven_plus");
        FileUtils.createDirectory(this.workHome);
        this.id = "";
        this.name = "";
        this.inputIntervalTime = 300;
        this.repositoryId = CentralMavenRepository.class.getAnnotation(EasyBean.class).value();
        this.autoSwitchTab = true;
        this.tabIndex = MavenSearchPluginSettings.DEFAULT_TAB_INDEX;
        this.elementPriority = MavenSearchPluginSettings.DEFAULT_ELEMENT_PRIORITY;
        this.tabVisible = true;
        this.expireTimeMillis = MavenSearchPluginSettings.DEFAULT_EXPIRE_TIME_MILLIS;
        this.searchInAllTab = false;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getInputIntervalTime() {
        return this.inputIntervalTime;
    }

    public void setInputIntervalTime(long continueInputIntervalTime) {
//        if (log.isDebugEnabled()) {
//            log.debug("setInputIntervalTime({})", continueInputIntervalTime);
//        }
        this.inputIntervalTime = continueInputIntervalTime;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
//        if (log.isDebugEnabled()) {
//            log.debug("setRepositoryId({})", repositoryId);
//        }
        this.repositoryId = repositoryId;
    }

    public void setAutoSwitchTab(boolean autoSwitchTab) {
//        if (log.isDebugEnabled()) {
//            log.debug("setAutoSwitchTab({})", autoSwitchTab);
//        }
        this.autoSwitchTab = autoSwitchTab;
    }

    public boolean isAutoSwitchTab() {
        return autoSwitchTab;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
//        if (log.isDebugEnabled()) {
//            log.debug("setTabIndex({})", tabIndex);
//        }
        this.tabIndex = tabIndex;
    }

    public int getElementPriority() {
        return elementPriority;
    }

    public void setElementPriority(int elementPriority) {
//        if (log.isDebugEnabled()) {
//            log.debug("setElementPriority({})", elementPriority);
//        }
        this.elementPriority = elementPriority;
    }

    public boolean isTabVisible() {
        return tabVisible;
    }

    public void setTabVisible(boolean tabVisible) {
//        if (log.isDebugEnabled()) {
//            log.debug("setTabVisible({})", tabVisible);
//        }
        this.tabVisible = tabVisible;
    }

    public long getExpireTimeMillis() {
        return expireTimeMillis;
    }

    public void setExpireTimeMillis(long expireTimeMillis) {
//        if (log.isDebugEnabled()) {
//            log.debug("setExpireTimeMillis({})", expireTimeMillis);
//        }
        this.expireTimeMillis = expireTimeMillis;
    }

    public boolean isUseAllTab() {
        return searchInAllTab;
    }

    public void setUseAllTab(boolean searchInAllTab) {
//        if (log.isDebugEnabled()) {
//            log.debug("setSearchInAllTab({})", searchInAllTab);
//        }
        this.searchInAllTab = searchInAllTab;
    }

    public File getWorkHome() {
        return workHome;
    }

    public void setWorkHome(File workHome) {
//        if (log.isDebugEnabled()) {
//            log.debug("setWorkHome({})", workHome);
//        }
        this.workHome = workHome;
    }
}
