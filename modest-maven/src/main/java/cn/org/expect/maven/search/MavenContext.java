package cn.org.expect.maven.search;

import java.util.List;
import javax.swing.*;

import cn.org.expect.maven.intellij.idea.navigation.SearchNavigation;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationItem;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationResultSet;
import cn.org.expect.maven.intellij.idea.MavenPluginContributor;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.Advertiser;

public class MavenContext {

    /** 事件 */
    private final AnActionEvent event;

    /** 搜素贡献者 */
    private volatile MavenPluginContributor contributor;

    /** 连续输入文本的间隔时间 */
    private long inputIntervalTime;

    /** 最后一次执行模糊查询的文本 */
    private volatile String searchPattern;

    /** IDea 编辑器中选中的文本 */
    private volatile String editorSelectText;

    /** 选中的导航记录 */
    private volatile SearchNavigation selectNavigationList;

    /** 选中的版本列表记录 */
    private volatile SearchNavigationItem selectNavigationItem;

    /** Idea查询对话框对象 */
    private volatile SearchEverywhereUI searchEverywhereUI;

    /** 查询结果中的列表 */
    private volatile JBList<Object> JBList;

    /** JBList 数据模型 */
    private volatile SearchListModel JBListModel;

    /** 查询结果最下面的广告栏 */
    private volatile Advertiser advertiser;

    /** 进度指示器 */
    private volatile ProgressIndicator progressIndicator;

    /** 最近一次模糊搜索结果 */
    private volatile MavenSearchResult mavenFinderResult;

    /** 搜索输入框 */
    private volatile JTextField searchField;

    /** 导航记录结果集 */
    private volatile SearchNavigationResultSet navigationResultSet;

    public MavenContext(AnActionEvent event) {
        this.event = Ensure.notNull(event);
        this.inputIntervalTime = 300;
    }

    public AnActionEvent getActionEvent() {
        return this.event;
    }

    public MavenPluginContributor getContributor() {
        return this.contributor;
    }

    public void setContributor(MavenPluginContributor contributor) {
        this.contributor = contributor;
    }

    /**
     * 返回最后一次模糊查询的文本
     *
     * @return 文本信息
     */
    public String getSearchPattern() {
        return searchPattern;
    }

    /**
     * 设置最后一次模糊查询的文本
     *
     * @param searchPattern 文本信息
     */
    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public long getInputIntervalTime() {
        return this.inputIntervalTime;
    }

    public void setInputIntervalTime(long continueInputIntervalTime) {
        this.inputIntervalTime = continueInputIntervalTime;
    }

    public synchronized void setPatternSearchResult(MavenSearchResult result) {
        Ensure.notNull(result);
        List<MavenArtifact> list = result.getList();
        for (MavenArtifact artifact : list) {
            artifact.setFold(true);
        }
        this.mavenFinderResult = result;
    }

    /**
     * 返回上一次查询结果
     *
     * @return 查询结果
     */
    public MavenSearchResult getPatternSearchResult() {
        return this.mavenFinderResult;
    }

    /**
     * 返回编辑器中选中的文本
     *
     * @return 文本信息
     */
    public String getEditorSelectText() {
        return this.editorSelectText;
    }

    /**
     * 编辑器中选中的文本
     *
     * @param editorSelectText 文本信息
     */
    public void setEditorSelectText(String editorSelectText) {
        this.editorSelectText = editorSelectText;
    }

    /**
     * 返回选中的导航栏
     *
     * @return 导航栏
     */
    public SearchNavigation getSelectCatalog() {
        return this.selectNavigationList;
    }

    /**
     * 设置选中的导航栏
     *
     * @param JBListSelectItem 导航栏
     */
    public void setSelectCatalog(SearchNavigation JBListSelectItem) {
        this.selectNavigationList = JBListSelectItem;
    }

    /**
     * 返回选中的版本列表记录
     *
     * @return 版本列表记录
     */
    public SearchNavigationItem getSelectItem() {
        return selectNavigationItem;
    }

    /**
     * 设置选中的版本列表记录
     *
     * @param selectNavigationItem 版本列表记录
     */
    public void setSelectItem(SearchNavigationItem selectNavigationItem) {
        this.selectNavigationItem = selectNavigationItem;
    }

    public SearchEverywhereUI getSearchEverywhereUI() {
        return this.searchEverywhereUI;
    }

    public void setSearchEverywhereUI(SearchEverywhereUI searchEverywhereUI) {
        this.searchEverywhereUI = searchEverywhereUI;
    }

    public JBList<Object> getJBList() {
        return this.JBList;
    }

    public void setJBList(JBList<Object> jbList) {
        this.JBList = jbList;
    }

    public Advertiser getAdvertiser() {
        return this.advertiser;
    }

    public void setAdvertiser(Advertiser advertiser) {
        this.advertiser = advertiser;
    }

    public SearchListModel getJBListModel() {
        return this.JBListModel;
    }

    public void setJBListModel(SearchListModel JBListModel) {
        this.JBListModel = JBListModel;
    }

    public ProgressIndicator getProgressIndicator() {
        return this.progressIndicator;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public void setSearchField(JTextField searchField) {
        this.searchField = searchField;
    }

    public SearchNavigationResultSet getNavigationResultSet() {
        return navigationResultSet;
    }

    public void setNavigationResultSet(SearchNavigationResultSet navigationResultSet) {
        this.navigationResultSet = navigationResultSet;
    }
}
