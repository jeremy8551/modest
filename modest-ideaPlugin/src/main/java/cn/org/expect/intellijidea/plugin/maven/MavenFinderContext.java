package cn.org.expect.intellijidea.plugin.maven;

import java.util.List;
import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigationList;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.Advertiser;

public class MavenFinderContext {

    /** 事件 */
    private final AnActionEvent event;

    /** 搜素贡献者 */
    private volatile SearchEverywhereContributor<?> contributor;

    /** 连续输入文本的间隔时间 */
    private long inputIntervalTime;

    /** IDea 编辑器中选中的文本 */
    private volatile String editorSelectText;

    /** 查找结果中选中记录的文本 */
    private volatile MavenFinderNavigationList JBListSelectItem;

    /** Idea查询对话框对象 */
    private volatile SearchEverywhereUI searchEverywhereUI;

    /** 查询结果中的列表 */
    private volatile JBList<Object> JBList;

    /** JBList 数据模型 */
    private volatile SearchListModel JBListModel;

    /** 查询结果中的列表所在的滚动组件 */
    private volatile JScrollPane scrollPane;

    /** 滚动组件所在的面板 */
    private volatile JPanel suggestionsPanel;

    /** 查询结果最下面的广告栏 */
    private volatile Advertiser advertiser;

    /** 进度指示器 */
    private volatile ProgressIndicator progressIndicator;

    /** 最近一次模糊搜索结果 */
    private volatile MavenArtifactSet mavenFinderResult;

    public MavenFinderContext(AnActionEvent event) {
        this.event = Ensure.notNull(event);
        this.inputIntervalTime = 300;
    }

    public AnActionEvent getActionEvent() {
        return this.event;
    }

    public SearchEverywhereContributor<?> getContributor() {
        return this.contributor;
    }

    public void setContributor(SearchEverywhereContributor<?> contributor) {
        this.contributor = contributor;
    }

    public long getInputIntervalTime() {
        return this.inputIntervalTime;
    }

    public void setInputIntervalTime(long continueInputIntervalTime) {
        this.inputIntervalTime = continueInputIntervalTime;
    }

    public synchronized void setPatternSearchResult(MavenArtifactSet result) {
        Ensure.notNull(result);
        List<MavenArtifact> list = result.getArtifacts();
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
    public MavenArtifactSet getPatternSearchResult() {
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
    public MavenFinderNavigationList getSelectItem() {
        return this.JBListSelectItem;
    }

    /**
     * 设置选中的导航栏
     *
     * @param JBListSelectItem 导航栏
     */
    public void setSelectItem(MavenFinderNavigationList JBListSelectItem) {
        this.JBListSelectItem = JBListSelectItem;
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

    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public JPanel getSuggestionsPanel() {
        return suggestionsPanel;
    }

    public void setSuggestionsPanel(JPanel suggestionsPanel) {
        this.suggestionsPanel = suggestionsPanel;
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
}
