package cn.org.expect.intellij.idea.plugin.maven.impl;

import java.awt.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigationList;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SimpleMavenSearchPluginContext implements MavenSearchPluginContext {

    private final MavenSearchPlugin plugin;

    /** 事件 */
    private final AnActionEvent event;

    /** 最后一次执行模糊查询的文本 */
    private volatile String searchPattern;

    /** 选中的版本列表记录 */
    private volatile MavenSearchNavigation selectNavigation;

    /** 最近一次模糊搜索结果 */
    private volatile ArtifactSearchResult mavenSearchResult;

    /** 显示搜索结果的位置信息 */
    private volatile Rectangle visibleRect;

    /** 导航信息 */
    private volatile MavenSearchNavigationList navigationList;

    public SimpleMavenSearchPluginContext(AnActionEvent event, MavenSearchPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
    }

    public AnActionEvent getActionEvent() {
        return this.event;
    }

    public String getSearchText() {
        return searchPattern;
    }

    public void setSearchText(String pattern) {
        this.searchPattern = pattern;
    }

    public void setSearchResult(ArtifactSearchResult result) {
        this.mavenSearchResult = result;
        this.navigationList = this.plugin.toNavigationList(result);
    }

    public ArtifactSearchResult getSearchResult() {
        return this.mavenSearchResult;
    }

    public MavenSearchNavigation getSelectNavigation() {
        return selectNavigation;
    }

    public void setSelectNavigation(MavenSearchNavigation selectNavigation) {
        this.selectNavigation = selectNavigation;
    }

    public Rectangle getVisibleRect() {
        return visibleRect;
    }

    public void setVisibleRect(Rectangle visibleRect) {
        this.visibleRect = visibleRect;
    }

    public MavenSearchNavigationList getNavigationList() {
        return navigationList;
    }

    public void setNavigationList(MavenSearchNavigationList navigationList) {
        this.navigationList = navigationList;
    }
}
