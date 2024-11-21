package cn.org.expect.intellij.idea.plugin.maven;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationItem;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearchContext;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MavenSearchPluginContext implements MavenSearchContext {
    private final static Log log = LogFactory.getLog(MavenSearchPluginContext.class);

    /** 事件 */
    private final AnActionEvent event;

    /** 最后一次执行模糊查询的文本 */
    private volatile String searchPattern;

    /** 选中的导航记录 */
    private volatile SearchNavigationHead selectedNavigation;

    /** 选中的版本列表记录 */
    private volatile SearchNavigationItem selectNavigationItem;

    /** 最近一次模糊搜索结果 */
    private volatile MavenSearchResult mavenSearchResult;

    /** true表示将UI固定在前端 */
    private volatile boolean pinWindow;

    public MavenSearchPluginContext(AnActionEvent event) {
        this.event = event;
        this.pinWindow = false;
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

    public boolean isPinWindow() {
        return pinWindow;
    }

    public void setPinWindow(boolean pinWindow) {
        this.pinWindow = pinWindow;
    }
}
