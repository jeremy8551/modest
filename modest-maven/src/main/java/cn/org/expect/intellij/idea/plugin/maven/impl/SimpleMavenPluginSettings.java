package cn.org.expect.intellij.idea.plugin.maven.impl;

import java.io.File;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.intellij.idea.plugin.maven.settings.MavenPluginSettings;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.central.CentralArtifactDownloader;
import cn.org.expect.maven.repository.central.CentralMavenRepository;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

/**
 * 插件配置信息
 */
@EasyBean(singleton = true)
public class SimpleMavenPluginSettings implements MavenPluginSettings {
    protected final static Log log = LogFactory.getLog(SimpleMavenPluginSettings.class);

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
    private volatile int navigationPriority;

    /** 失效时间（单位毫秒） */
    private volatile long expireTimeMillis;

    /** true表示支持在 All 标签页中执行查询操作 */
    private volatile boolean searchInAllTab;

    /** 下载文件的地址 */
    private volatile String downloadWay;

    /** true表示读取父工程POM的项目信息，false表示只读本工程POM中的项目信息 */
    private volatile boolean useParentPom;

    public SimpleMavenPluginSettings() {
        this.workHome = new File(Settings.getUserHome(), ".maven_plus");
        FileUtils.createDirectory(this.workHome);
        this.id = "";
        this.name = "";
        this.inputIntervalTime = 300;
        this.repositoryId = CentralMavenRepository.class.getAnnotation(EasyBean.class).value();
        this.autoSwitchTab = true;
        this.tabIndex = 10000;
        this.navigationPriority = 50;
        this.tabVisible = true;
        this.expireTimeMillis = 1000 * 3600 * 24;
        this.searchInAllTab = false;
        this.downloadWay = CentralArtifactDownloader.class.getAnnotation(EasyBean.class).value();
        this.useParentPom = false;
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

    public void setInputIntervalTime(long millis) {
        this.inputIntervalTime = millis;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public void setAutoSwitchTab(boolean autoSwitchTab) {
        this.autoSwitchTab = autoSwitchTab;
    }

    public boolean isAutoSwitchTab() {
        return autoSwitchTab;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int index) {
        this.tabIndex = index;
    }

    public int getNavigationPriority() {
        return navigationPriority;
    }

    public void setNavigationPriority(int navigationPriority) {
        this.navigationPriority = navigationPriority;
    }

    public boolean isTabVisible() {
        return tabVisible;
    }

    public void setTabVisible(boolean value) {
        this.tabVisible = value;
    }

    public long getExpireTimeMillis() {
        return expireTimeMillis;
    }

    public void setExpireTimeMillis(long millis) {
        this.expireTimeMillis = millis;
    }

    public boolean isUseAllTab() {
        return searchInAllTab;
    }

    public void setUseAllTab(boolean searchInAllTab) {
        this.searchInAllTab = searchInAllTab;
    }

    public File getWorkHome() {
        return workHome;
    }

    public void setWorkHome(File workHome) {
        this.workHome = workHome;
    }

    public String getDownloadWay() {
        return downloadWay;
    }

    public void setDownloadWay(String downSource) {
        this.downloadWay = downSource;
    }

    public boolean isUseParentPom() {
        return useParentPom;
    }

    public void setUseParentPom(boolean useParentPom) {
        this.useParentPom = useParentPom;
    }

    /**
     * 将配置信息持久化到文件
     *
     * @param filename 文件名
     */
    public void save(String filename) {
        MavenPluginSettings settings = this;
        File file = new File(settings.getWorkHome(), filename);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(settings);

            if (log.isDebugEnabled()) {
                log.debug("save {}, {}, {}", settings.getClass().getSimpleName(), file.getAbsolutePath(), jsonStr);
            }

            FileUtils.write(file, CharsetName.UTF_8, false, jsonStr);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 加载配置文件
     *
     * @param filename 文件名
     */
    public void load(String filename) {
        MavenPluginSettings settings = this;
        File file = new File(settings.getWorkHome(), filename);
        try {
            if (file.exists() && file.isFile()) {
                String jsonStr = FileUtils.readline(file, CharsetName.UTF_8, 0);

                if (log.isDebugEnabled()) {
                    log.debug("load {}, {}", MavenPluginSettings.class.getSimpleName(), jsonStr);
                }

                // 默认值
                SimpleMavenPluginSettings def = new SimpleMavenPluginSettings();

                JSONObject jsonObject = new JSONObject(jsonStr);
                long inputIntervalTime = jsonObject.optLong("inputIntervalTime", def.getInputIntervalTime());
                String repositoryId = jsonObject.optString("repositoryId", def.getRepositoryId());
                boolean autoSwitchTab = jsonObject.optBoolean("autoSwitchTab", def.isAutoSwitchTab());
                int tabIndex = jsonObject.optInt("tabIndex", def.getTabIndex());
                boolean tabVisible = jsonObject.optBoolean("tabVisible", def.isTabVisible());
                int elementPriority = jsonObject.optInt("elementPriority", def.getNavigationPriority());
                long expireTimeMillis = jsonObject.optLong("expireTimeMillis", def.getExpireTimeMillis());
                boolean searchInAllTab = jsonObject.optBoolean("useAllTab", def.isUseAllTab());
                String downloadWay = jsonObject.optString("downloadWay", def.getDownloadWay());

                settings.setInputIntervalTime(inputIntervalTime);
                settings.setRepositoryId(repositoryId);
                settings.setAutoSwitchTab(autoSwitchTab);
                settings.setTabIndex(tabIndex);
                settings.setTabVisible(tabVisible);
                settings.setNavigationPriority(elementPriority);
                settings.setExpireTimeMillis(expireTimeMillis);
                settings.setUseAllTab(searchInAllTab);
                settings.setDownloadWay(downloadWay);
            }
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
