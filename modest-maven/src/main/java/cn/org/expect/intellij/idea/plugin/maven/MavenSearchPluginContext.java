package cn.org.expect.intellij.idea.plugin.maven;

import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationItem;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearchContext;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.util.Ensure;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MavenSearchPluginContext implements MavenSearchContext {

    /** 事件 */
    private final AnActionEvent event;

    /** 连续输入文本的间隔时间 */
    private long inputIntervalTime;

    /** 远程 Maven 仓库的标识符，就是 {@linkplain EasyBean#value()} */
    private volatile String repositoryId;

    /** 最后一次执行模糊查询的文本 */
    private volatile String searchPattern;

    /** 选中的导航记录 */
    private volatile SearchNavigationHead selectedNavigation;

    /** 选中的版本列表记录 */
    private volatile SearchNavigationItem selectNavigationItem;

    /** 最近一次模糊搜索结果 */
    private volatile MavenSearchResult mavenSearchResult;

    /** 如果选中文本是 groupId:artifactId:version 时，是否自动切换tab */
    private volatile boolean autoSwitchTab;

    /** 标签页所在的位置，从0开始 */
    private volatile int tabIndex;

    /** 标签名 */
    private volatile String tabName;

    /** true 表示显示标签页 */
    private volatile boolean tabVisible;

    /** 查询结果的排序权重 */
    private volatile int elementPriority;

    /** 失效时间（单位毫秒） */
    private volatile long expireTimeMillis;

    /** true表示将UI固定在前端 */
    private volatile boolean pinWindow;

    /** true表示支持在 All 标签页中执行查询操作 */
    private volatile boolean searchInAllTab;

    public MavenSearchPluginContext(AnActionEvent event) {
        this.event = Ensure.notNull(event);
        this.inputIntervalTime = 300;
        this.repositoryId = MavenRepository.DEFAULT_SELECTED_REPOSITORY;
        this.autoSwitchTab = true;
        this.tabIndex = 0;
        this.tabName = MavenSearchMessage.get("maven.search.tab.name");
        this.tabName = "Repository";
        this.elementPriority = 50;
        this.tabVisible = true;
        this.expireTimeMillis = 1000 * 3600 * 24; // 默认一天有效
        this.pinWindow = false;
        this.searchInAllTab = false;
    }

    public AnActionEvent getActionEvent() {
        return this.event;
    }

    public String getSearchText() {
        return searchPattern;
    }

    public void setSearchText(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public long getInputIntervalTime() {
        return this.inputIntervalTime;
    }

    public void setInputIntervalTime(long continueInputIntervalTime) {
        this.inputIntervalTime = continueInputIntervalTime;
    }

    public synchronized void setSearchResult(MavenSearchResult result) {
        if (result != null) {
            List<MavenArtifact> list = result.getList();
            for (MavenArtifact artifact : list) {
                artifact.setFold(true);
            }
        }
        this.mavenSearchResult = result;
    }

    public MavenSearchResult getSearchResult() {
        return this.mavenSearchResult;
    }

    /**
     * 返回选中的导航栏
     *
     * @return 导航栏
     */
    public SearchNavigationHead getSelectNavigationHead() {
        return this.selectedNavigation;
    }

    /**
     * 设置选中的导航栏
     *
     * @param navigation 导航栏
     */
    public void setSelectNavigationHead(SearchNavigationHead navigation) {
        this.selectedNavigation = navigation;
    }

    /**
     * 返回选中的版本列表记录
     *
     * @return 版本列表记录
     */
    public SearchNavigationItem getSelectNavigationItem() {
        return selectNavigationItem;
    }

    /**
     * 设置选中的版本列表记录
     *
     * @param selectNavigationItem 版本列表记录
     */
    public void setSelectNavigationItem(SearchNavigationItem selectNavigationItem) {
        this.selectNavigationItem = selectNavigationItem;
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

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public int getElementPriority() {
        return elementPriority;
    }

    public void setElementPriority(int elementPriority) {
        this.elementPriority = elementPriority;
    }

    public boolean isTabVisible() {
        return tabVisible;
    }

    public void setTabVisible(boolean tabVisible) {
        this.tabVisible = tabVisible;
    }

    public long getExpireTimeMillis() {
        return expireTimeMillis;
    }

    public void setExpireTimeMillis(long expireTimeMillis) {
        this.expireTimeMillis = expireTimeMillis;
    }

    public boolean isPinWindow() {
        return pinWindow;
    }

    public void setPinWindow(boolean pinWindow) {
        this.pinWindow = pinWindow;
    }

    public boolean isSearchInAllTab() {
        return searchInAllTab;
    }

    public void setSearchInAllTab(boolean searchInAllTab) {
        this.searchInAllTab = searchInAllTab;
    }
}
