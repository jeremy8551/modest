package cn.org.expect.intellij.idea.plugin.maven.impl;

import java.awt.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.maven.search.SearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchEverywhereNavigationCollection;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SimpleMavenSearchPluginContext implements MavenSearchPluginContext {

    /** 事件 */
    private final AnActionEvent event;

    /** 选中的版本列表记录 */
    private volatile SearchNavigation selectedNavigation;

    /** 最近一次模糊搜索结果 */
    private volatile ArtifactSearchResult searchResult;

    /** 显示搜索结果的位置信息 */
    private volatile Rectangle visibleRect;

    /** 导航信息 */
    private volatile SearchEverywhereNavigationCollection navigationList;

    public SimpleMavenSearchPluginContext(AnActionEvent event) {
        this.event = event;
    }

    public AnActionEvent getActionEvent() {
        return this.event;
    }

    public void setSearchResult(ArtifactSearchResult result) {
        this.searchResult = result;
    }

    public ArtifactSearchResult getSearchResult() {
        return this.searchResult;
    }

    public SearchNavigation geSelectedNavigation() {
        return selectedNavigation;
    }

    public void setSelectedNavigation(SearchNavigation selectNavigation) {
        this.selectedNavigation = selectNavigation;
    }

    public Rectangle getVisibleRect() {
        return visibleRect;
    }

    public void setVisibleRect(Rectangle visibleRect) {
        this.visibleRect = visibleRect;
    }

    public SearchEverywhereNavigationCollection getNavigationList() {
        return navigationList;
    }

    public void setNavigationList(SearchEverywhereNavigationCollection navigationList) {
        this.navigationList = navigationList;
    }
}
