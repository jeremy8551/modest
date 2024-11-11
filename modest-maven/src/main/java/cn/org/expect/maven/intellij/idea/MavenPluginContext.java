package cn.org.expect.maven.intellij.idea;

import java.util.List;
import javax.swing.*;

import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationHead;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationItem;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationResultSet;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearchContext;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereHeader;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.Advertiser;

public class MavenPluginContext implements MavenSearchContext {

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
    private volatile SearchNavigationHead selectedNavigation;

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
    private volatile MavenSearchResult mavenSearchResult;

    /** 搜索输入框 */
    private volatile JTextField searchField;

    /** 导航记录结果集 */
    private volatile SearchNavigationResultSet navigationResultSet;

    private volatile SearchEverywhereHeader myHeader;

    /** 加载上下文信息的状态，true表示加载完毕 false表示还未加载 */
    private volatile boolean loadStatus;

    public MavenPluginContext(AnActionEvent event) {
        this.event = Ensure.notNull(event);
        this.inputIntervalTime = 300;
        this.loadStatus = false;
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

    @Override
    public String getSearchText() {
        return searchPattern;
    }

    @Override
    public void setSearchText(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    @Override
    public long getInputIntervalTime() {
        return this.inputIntervalTime;
    }

    @Override
    public void setInputIntervalTime(long continueInputIntervalTime) {
        this.inputIntervalTime = continueInputIntervalTime;
    }

    @Override
    public synchronized void setSearchResult(MavenSearchResult result) {
        Ensure.notNull(result);
        List<MavenArtifact> list = result.getList();
        for (MavenArtifact artifact : list) {
            artifact.setFold(true);
        }
        this.mavenSearchResult = result;
    }

    @Override
    public MavenSearchResult getSearchResult() {
        return this.mavenSearchResult;
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
    public SearchNavigationHead getSelectedNavigation() {
        return this.selectedNavigation;
    }

    /**
     * 设置选中的导航栏
     *
     * @param navigation 导航栏
     */
    public void setSelectedNavigation(SearchNavigationHead navigation) {
        this.selectedNavigation = navigation;
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

    protected void waitForLoad() {
        long timeout = 3000;
        long startMillis = System.currentTimeMillis();
        while (!this.loadStatus && System.currentTimeMillis() - startMillis <= timeout) {
            Dates.sleep(200);
        }
    }

    public SearchEverywhereUI getSearchEverywhereUI() {
        this.waitForLoad();
        return this.searchEverywhereUI;
    }

    public void setSearchEverywhereUI(SearchEverywhereUI searchEverywhereUI) {
        this.searchEverywhereUI = searchEverywhereUI;
    }

    public JBList<Object> getJBList() {
        this.waitForLoad();
        return this.JBList;
    }

    public void setJBList(JBList<Object> jbList) {
        this.JBList = jbList;
    }

    public Advertiser getAdvertiser() {
        this.waitForLoad();
        return this.advertiser;
    }

    public void setAdvertiser(Advertiser advertiser) {
        this.advertiser = advertiser;
    }

    public SearchListModel getJBListModel() {
        this.waitForLoad();
        return this.JBListModel;
    }

    public void setJBListModel(SearchListModel JBListModel) {
        this.JBListModel = JBListModel;
    }

    public ProgressIndicator getProgressIndicator() {
        this.waitForLoad();
        return this.progressIndicator;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    public JTextField getSearchField() {
        this.waitForLoad();
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

    public SearchEverywhereHeader getMyHeader() {
        this.waitForLoad();
        return myHeader;
    }

    public void setMyHeader(SearchEverywhereHeader myHeader) {
        this.myHeader = myHeader;
    }

    public void setLoadStatus(boolean detected) {
        this.loadStatus = detected;
    }
}
