package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.intellij.idea.plugin.maven.settings.DownloadWay;
import cn.org.expect.intellij.idea.plugin.maven.settings.MavenSearchPluginSettingsImpl;
import cn.org.expect.maven.search.ArtifactSearchSettings;

public interface MavenSearchPluginSettings extends ArtifactSearchSettings {

    /** 默认一天有效 */
    int DEFAULT_EXPIRE_TIME_MILLIS = 1000 * 3600 * 24;

    /** 50 */
    int DEFAULT_ELEMENT_PRIORITY = 50;

    /** 在最右侧 */
    int DEFAULT_TAB_INDEX = 10000;

    /**
     * 设置插件ID
     *
     * @param id 插件ID
     */
    void setId(String id);

    /**
     * 返回插件ID
     *
     * @return 插件ID
     */
    String getId();

    /**
     * 如果选中文本是 groupId:artifactId:version 时，是否自动切换tab
     *
     * @return
     */
    boolean isAutoSwitchTab();

    /**
     * 设置是否自动切换tab
     *
     * @param autoSwitchTab
     */
    void setAutoSwitchTab(boolean autoSwitchTab);

    /**
     * 标签页所在的位置，从0开始
     *
     * @return
     */
    int getTabIndex();

    /**
     * 设置标签页所在的位置，从0开始
     *
     * @param tabIndex
     */
    void setTabIndex(int tabIndex);

    /**
     * true 表示显示标签页
     *
     * @return
     */
    boolean isTabVisible();

    /**
     * true 表示显示标签页
     *
     * @param tabVisible
     */
    void setTabVisible(boolean tabVisible);

    /**
     * true表示支持在 All 标签页中执行查询操作
     *
     * @return
     */
    boolean isUseAllTab();

    /**
     * true表示支持在 All 标签页中执行查询操作
     *
     * @param searchInAllTab
     */
    void setUseAllTab(boolean searchInAllTab);

    /**
     * 查询结果的排序权重
     *
     * @return
     */
    int getElementPriority();

    /**
     * 查询结果的排序权重
     *
     * @param elementPriority
     */
    void setElementPriority(int elementPriority);

    /**
     * 下载工件的方式
     *
     * @return 下载方式
     */
    DownloadWay getDownloadWay();

    /**
     * 设置下载工件的方式
     *
     * @param downSource 下载方式
     */
    void setDownloadWay(DownloadWay downSource);

    /**
     * 持久化配置信息
     */
    default void save() {
        MavenSearchPluginUtils.save(this);
    }

    /**
     * 加载配置信息
     */
    default void load() {
        MavenSearchPluginUtils.load(this);
    }

    /**
     * 返回一个副本
     *
     * @return 副本
     */
    default MavenSearchPluginSettings copy() {
        MavenSearchPluginSettingsImpl context = new MavenSearchPluginSettingsImpl();
        context.setId(this.getId());
        context.setName(this.getName());
        context.setWorkHome(this.getWorkHome());
        context.setUseAllTab(this.isUseAllTab());
        context.setElementPriority(this.getElementPriority());
        context.setTabVisible(this.isTabVisible());
        context.setTabIndex(this.getTabIndex());
        context.setExpireTimeMillis(this.getExpireTimeMillis());
        context.setAutoSwitchTab(this.isAutoSwitchTab());
        context.setRepositoryId(this.getRepositoryId());
        context.setInputIntervalTime(this.getInputIntervalTime());
        context.setDownloadWay(this.getDownloadWay());
        return context;
    }

    /**
     * 合并输入参数
     *
     * @param context 输入参数
     */
    default MavenSearchPluginSettings merge(MavenSearchPluginSettings context) {
        this.setUseAllTab(context.isUseAllTab());
        this.setElementPriority(context.getElementPriority());
        this.setTabVisible(context.isTabVisible());
        this.setTabIndex(context.getTabIndex());
        this.setExpireTimeMillis(context.getExpireTimeMillis());
        this.setAutoSwitchTab(context.isAutoSwitchTab());
        this.setRepositoryId(context.getRepositoryId());
        this.setInputIntervalTime(context.getInputIntervalTime());
        this.setDownloadWay(context.getDownloadWay());
        return this;
    }

    /**
     * 判断配置信息是否相等
     *
     * @param settings 上下文信息
     * @return 返回true表示不同
     */
    default boolean isEquals(MavenSearchPluginSettings settings) {
        return settings != null //
                && settings.isUseAllTab() == this.isUseAllTab() //
                && settings.getElementPriority() == this.getElementPriority() //
                && settings.isTabVisible() == this.isTabVisible() //
                && settings.getTabIndex() == this.getTabIndex() //
                && settings.getExpireTimeMillis() == this.getExpireTimeMillis() //
                && settings.isAutoSwitchTab() == this.isAutoSwitchTab() //
                && settings.getRepositoryId().equals(this.getRepositoryId()) //
                && settings.getInputIntervalTime() == this.getInputIntervalTime() //
                && settings.getDownloadWay() == this.getDownloadWay() //
                ;
    }
}
