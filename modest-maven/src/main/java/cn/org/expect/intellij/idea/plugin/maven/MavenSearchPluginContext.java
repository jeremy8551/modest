package cn.org.expect.intellij.idea.plugin.maven;

import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationItem;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationResultSet;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearchContext;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MavenSearchPluginContext implements MavenSearchContext {

    /** 事件 */
    private final AnActionEvent event;

    /** 连续输入文本的间隔时间 */
    private long inputIntervalTime;

    /** 远程 Maven 仓库的标识符，就是 {@linkplain EasyBean#value()} */
    private volatile String remoteRepositoryName;

    /** 最后一次执行模糊查询的文本 */
    private volatile String searchPattern;

    /** 选中的导航记录 */
    private volatile SearchNavigationHead selectedNavigation;

    /** 选中的版本列表记录 */
    private volatile SearchNavigationItem selectNavigationItem;

    /** Idea查询对话框对象 */
    private volatile SearchEverywhereUI searchEverywhereUI;

    /** 最近一次模糊搜索结果 */
    private volatile MavenSearchResult mavenSearchResult;

    /** 导航记录结果集 */
    private volatile SearchNavigationResultSet navigationResultSet;

    /** 如果选中文本是groupid:artifactid:version时，是否自动切换tab */
    private volatile boolean autoSwitchTab;

    /** 状态栏信息 */
    private volatile String advertiserText;

    public MavenSearchPluginContext(AnActionEvent event) {
        this.event = Ensure.notNull(event);
        this.inputIntervalTime = 300;
        this.remoteRepositoryName = "central";
        this.autoSwitchTab = true;
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
        Ensure.notNull(result);
        List<MavenArtifact> list = result.getList();
        for (MavenArtifact artifact : list) {
            artifact.setFold(true);
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

    public SearchEverywhereUI getSearchEverywhereUI() {
        return this.searchEverywhereUI;
    }

    public void setSearchEverywhereUI(SearchEverywhereUI searchEverywhereUI) {
        this.searchEverywhereUI = searchEverywhereUI;
    }

    public SearchNavigationResultSet getNavigationResultSet() {
        return navigationResultSet;
    }

    public void setNavigationResultSet(SearchNavigationResultSet navigationResultSet) {
        this.navigationResultSet = navigationResultSet;
    }

    public String getRemoteRepositoryName() {
        return remoteRepositoryName;
    }

    public void setRemoteRepositoryName(String remoteRepositoryName) {
        this.remoteRepositoryName = remoteRepositoryName;
    }

    public void setAutoSwitchTab(boolean autoSwitchTab) {
        this.autoSwitchTab = autoSwitchTab;
    }

    public boolean isAutoSwitchTab() {
        return autoSwitchTab;
    }

    public String getAdvertiserText(Object obj) {
//        if (obj instanceof MavenSearchNavigation) { // 根据不同的类型
//            return this.advertiserText;
//        } else {
//            return "";
//        }
        return this.advertiserText;
    }

    public void setAdvertiserText(String advertiserText) {
        this.advertiserText = advertiserText;
    }
}
