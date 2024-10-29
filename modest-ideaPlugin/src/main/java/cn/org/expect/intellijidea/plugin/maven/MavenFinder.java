package cn.org.expect.intellijidea.plugin.maven;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderBlankItem;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderFoundElementInfo;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigation;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigationList;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigationItem;
import cn.org.expect.intellijidea.plugin.maven.navigation.NavigationItemComparator;
import cn.org.expect.intellijidea.plugin.maven.search.AsyncDatabaseSearch;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.Advertiser;

public class MavenFinder extends AsyncDatabaseSearch {
    private static final Logger log = Logger.getInstance(MavenFinder.class);

    private final MavenFinderContext context;

    public MavenFinder(MavenFinderContext context) {
        this.context = Ensure.notNull(context);
    }

    /**
     * 返回上下文信息
     *
     * @return 上下文信息
     */
    public MavenFinderContext getContext() {
        return context;
    }

    /**
     * 返回搜索贡献者
     *
     * @return 搜索贡献者
     */
    public MavenFinderContributor getContributor() {
        MavenFinderContributor contributor = new MavenFinderContributor(this);  // 创建 Idea 搜索的贡献者
        this.context.setContributor(contributor);
        return contributor;
    }

    /**
     * 多线程执行模糊搜索
     *
     * @param pattern 字符串
     */
    public void asyncSearch(String pattern) {
        this.getSearchPattern().search(this, pattern);
    }

    /**
     * 多线程执行精确搜索
     *
     * @param groupId    域名
     * @param artifactId 工件名
     */
    public void asyncSearch(String groupId, String artifactId) {
        this.getSearchExtra().search(this, groupId, artifactId);
    }

    /**
     * 检测Idea的UI组件
     *
     * @param event 事件
     */
    public void detectIdeaComponent(AnActionEvent event) {
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
        long startMillis = System.currentTimeMillis();
        while (!manager.isShown()) { // 等待对话框显示
            if (System.currentTimeMillis() - startMillis >= 3000) {
                break;
            } else {
                Dates.sleep(100);
            }
        }

        MavenFinderContext context = this.context;
        SearchEverywhereUI ui = manager.getCurrentlyShownUI();
        context.setSearchEverywhereUI(ui);

        try {
            JBList<Object> jbList = JavaDialectFactory.get().getField(ui, "myResultsList");
            context.setJBList(jbList);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            SearchListModel listModel = JavaDialectFactory.get().getField(ui, "myListModel");
            context.setJBListModel(listModel);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            ProgressIndicator progressIndicator = JavaDialectFactory.get().getField(ui, "mySearchProgressIndicator");
            context.setProgressIndicator(progressIndicator);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            JPanel jpanel = JavaDialectFactory.get().getField(ui, "suggestionsPanel");
            context.setSuggestionsPanel(jpanel);

            JScrollPane scrollPane = (JScrollPane) ((BorderLayout) jpanel.getLayout()).getLayoutComponent(jpanel, BorderLayout.CENTER);
            context.setScrollPane(scrollPane);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            Advertiser advertiser = JavaDialectFactory.get().getField(ui, "myHintLabel");
            context.setAdvertiser(advertiser);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean notMavenFinderTab() {
        SearchEverywhereUI everywhereUI = this.context.getSearchEverywhereUI();
        if (everywhereUI == null) {
            return false;
        }

        // 如果搜索的标签页不是 MavenFinder，就不显示广告信息
        String selectedTabID = everywhereUI.getSelectedTabID();
        return !this.context.getContributor().getSearchProviderId().equals(selectedTabID);
    }

    /**
     * 更新 SearchEverywhereUI 最下方广告栏中的信息
     *
     * @param message 文本信息
     * @param icon    图标
     */
    public void setAdvertiser(String message, Icon icon) {
        if (this.notMavenFinderTab()) {
            return;
        }

        Advertiser advertiser = this.context.getAdvertiser();
        if (advertiser == null) {
            return;
        }

        if (icon == MavenFinderIcon.BOTTOM || icon == MavenFinderIcon.BOTTOM_WAITING) {
            message = "<html><span style='color:orange;'>" + message + "</span></html>";
        } else if (icon == MavenFinderIcon.BOTTOM_ERROR) {
            message = "<html><span style='color:red;'>" + message + "</span></html>";
        }

        try {
            JLabel myTextPanel = JavaDialectFactory.get().getField(advertiser, "myTextPanel");
            myTextPanel.setText(message);
            myTextPanel.setIcon(icon);
            myTextPanel.repaint();

            JLabel myNextLabel = JavaDialectFactory.get().getField(advertiser, "myNextLabel");
            myNextLabel.setText(null);
            myNextLabel.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 设置提醒文本 <br>
     * 不能使用 Idea 的渲染线程执行这个方法，需要有单独的线程
     *
     * @param message 文本信息
     */
    public void setReminderText(String message) {
        if (this.notMavenFinderTab()) {
            return;
        }

        JBList<Object> jbList = this.context.getJBList();
        if (jbList != null) {
            jbList.setEmptyText(message);
        }
    }

    /**
     * 等待 SearchEverywhereUI 组件渲染完毕
     *
     * @param timeoutMillis 超时时间，单位：毫秒
     */
    public void waitForSearchEverywhereUI(long timeoutMillis) {
        ProgressIndicator progress = this.context.getProgressIndicator();
        if (progress != null) {
            long startMillis = System.currentTimeMillis();
            while (progress.isRunning() && System.currentTimeMillis() - startMillis >= timeoutMillis) {
                Dates.sleep(100);
            }
        }
    }

    /**
     * 使用最新的查询结果，渲染 UI 界面
     */
    public synchronized void repaint() {
        MavenArtifactSet result = this.context.getPatternSearchResult();
        this.repaint(result);
    }

    /**
     * 使用参数指定的查询结果，渲染 UI 界面
     *
     * @param result 查询结果
     */
    public synchronized void repaint(MavenArtifactSet result) {
        SearchListModel listModel = this.context.getJBListModel();
        if (listModel.getClass().getSimpleName().equals("MixedSearchListModel")) {
            try {
                JavaDialectFactory.get().setField(listModel, "myElementsComparator", new NavigationItemComparator());
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        // 将查询结果转为导航记录
        java.util.List<MavenFinderNavigation> list = this.toNavigationList(result);

        // 将导航记录合并到数据模型中
        this.mergeNavigation(listModel, list);

        JBList<Object> jbList = this.context.getJBList();
        log.info("repaint " + jbList + ", size: " + jbList.getModel().getSize());

        // 选中记录
        this.setSelection(jbList, listModel);

        // 渲染 JBList
        try {
            jbList.repaint();
            jbList.revalidate();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        // 设置广告信息
        int size = (result == null) ? 0 : result.size();
        String message = MavenFinderMessage.REMOTE_SEARCH_RESULT.fill(size);
        this.setAdvertiser(message, MavenFinderIcon.BOTTOM);
    }

    /**
     * 选中记录
     *
     * @param jbList    组件
     * @param listModel 组件的数据模型
     */
    protected void setSelection(JBList<Object> jbList, SearchListModel listModel) {
        MavenFinderNavigationList selectedItem = this.context.getSelectItem();
        if (selectedItem != null) {
            int selectedIndex = -1;
            for (int i = listModel.getSize() - 1; i >= 0; i--) {
                Object object = listModel.getElementAt(i);
                if (object instanceof MavenFinderNavigationList) {
                    MavenFinderNavigationList item = (MavenFinderNavigationList) object;
                    if (selectedItem.getArtifact().equals(item.getArtifact()) && item.getArtifact().isUnfold()) {
                        selectedIndex = i;

                        // 设置左侧等待图标
                        MavenArtifact artifact = selectedItem.getArtifact();
                        if (artifact.isUnfold() && this.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId()) == null) {
                            item.setIcon(MavenFinderIcon.LEFT_WAITING);
                        }
                        break;
                    }
                }
            }

            if (selectedIndex == -1) {
                jbList.clearSelection();
            } else {
                jbList.setSelectedIndex(selectedIndex);
            }
        }
    }

    /**
     * 将查询结果转为导航记录
     *
     * @param result 查询结果
     * @return 导航记录的集合
     */
    protected java.util.List<MavenFinderNavigation> toNavigationList(MavenArtifactSet result) {
        if (result == null) {
            return new ArrayList<MavenFinderNavigation>(0);
        }

        java.util.List<MavenArtifact> artifacts = result.getArtifacts();
        int size = artifacts.size();
        java.util.List<MavenFinderNavigation> list = new ArrayList<MavenFinderNavigation>(size);
        for (MavenArtifact artifact : artifacts) {
            MavenFinderNavigationList item = new MavenFinderNavigationList(artifact);
            list.add(item);

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();

            MavenArtifactSet artifactList = this.getDatabase().select(groupId, artifactId);
            boolean exists = artifactList != null;
            if (exists) {
                item.setIcon(MavenFinderIcon.LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (exists) {
                    item.setIcon(MavenFinderIcon.LEFT_UNFOLD);
                    for (MavenArtifact version : artifactList.getArtifacts()) {
                        MavenFinderNavigationItem navigation = new MavenFinderNavigationItem(version);
                        if (this.getLocalMavenRepository().exists(version)) {
                            navigation.setIcon(MavenFinderIcon.RIGHT_LOCAL);
                        }
                        list.add(navigation);
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (this.getSearchExtra().isExtraQuerying(groupId, artifactId)) {
                item.setIcon(MavenFinderIcon.LEFT_WAITING);
            }
        }
        return list;
    }

    /**
     * 将导航记录添加到数据模型中
     *
     * @param listModel 数据模型
     * @param list      导航记录
     */
    protected void mergeNavigation(SearchListModel listModel, java.util.List<MavenFinderNavigation> list) {
        // 删除（自动添加的）导航记录
        for (int i = listModel.getSize() - 1; i >= 0; i--) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            if (info.getElement() instanceof MavenFinderBlankItem) {
                try {
                    listModel.removeElement(info.getElement(), info.getContributor());
                } catch (Throwable ignored) { // 如果不能删除，则将导航记录清空，排序时放到最后
                }
            }
        }

        // 添加导航记录
        int i = 0;
        Iterator<MavenFinderNavigation> it = list.iterator();
        for (; i < listModel.getSize(); i++) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            if (info instanceof MavenFinderFoundElementInfo) {
                MavenFinderFoundElementInfo element = (MavenFinderFoundElementInfo) info;
                if (it.hasNext()) { // 向 ListModel 添加记录
                    MavenFinderNavigation item = it.next();
                    element.setElement(item);
                    element.setContributor(this.context.getContributor());
                } else {
                    break;
                }
            }
        }

        // 删除导航记录（注意：删除操作与上面的添加操作不能写到一起）
        for (int j = listModel.getSize() - 1; j >= i; j--) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(j);
            if (info instanceof MavenFinderFoundElementInfo) {
                MavenFinderFoundElementInfo element = (MavenFinderFoundElementInfo) info;
                MavenFinderNavigation item = element.getElement();
                if (item != null) {
                    try {
                        listModel.removeElement(item, info.getContributor());
                    } catch (Throwable e) { // 如果不能删除导航记录，则将导航记录清空
                        element.setElement(null);
                    }
                }
            }
        }

        // 向 ListModel 添加记录
        if (it.hasNext()) {
            java.util.List<MavenFinderFoundElementInfo> newList = new ArrayList<MavenFinderFoundElementInfo>(list.size());
            do {
                MavenFinderNavigation navigation = it.next();
                MavenFinderFoundElementInfo info = new MavenFinderFoundElementInfo(navigation, this.context.getContributor());
                newList.add(info);
            } while (it.hasNext());

            try {
                listModel.addElements(newList);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * 返回在编辑器中选中的文本
     */
    public String getEditorSelectText() {
        Editor editor = this.context.getActionEvent().getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            String selectedText = StringUtils.trimBlank(editor.getSelectionModel().getSelectedText());
            if (StringUtils.isNotBlank(selectedText)) {
                log.warn("--->      Selected text: " + selectedText);
                return selectedText;
            }
        }
        return null;
    }
}
