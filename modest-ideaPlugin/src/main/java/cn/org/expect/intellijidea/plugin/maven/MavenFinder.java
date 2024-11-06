package cn.org.expect.intellijidea.plugin.maven;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderBlankItem;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderFoundElementInfo;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigation;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigationCatalog;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigationItem;
import cn.org.expect.intellijidea.plugin.maven.navigation.NavigationItemComparator;
import cn.org.expect.intellijidea.plugin.maven.search.AsyncDatabaseSearch;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.MessageFormatter;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.Advertiser;
import org.jetbrains.annotations.NotNull;

public class MavenFinder extends AsyncDatabaseSearch {
    private static final Logger log = Logger.getInstance(MavenFinder.class);

    private final MavenFinderContext context;

    public MavenFinder(MavenFinderContext context) {
        super();
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
        this.context.setSearchPattern(pattern);
        this.getInputSearch().search(this, pattern);
    }

    /**
     * 多线程执行精确搜索
     *
     * @param groupId    域名
     * @param artifactId 工件名
     */
    public void asyncSearch(String groupId, String artifactId) {
        this.getSearch().searchExtra(this, groupId, artifactId);
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

        try {
            JTextField searchField = context.getSearchEverywhereUI().getSearchField();
            context.setSearchField(searchField);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 自动选择选项卡
     */
    public void switchToTab() {
        AnActionEvent event = this.context.getActionEvent();
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
        manager.setSelectedTabID(this.context.getContributor().getSearchProviderId()); // 选择标签页
    }

    /**
     * 将文本信息复制到剪切板中
     *
     * @param text 文本信息
     */
    public void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    /**
     * 推送正常通知
     *
     * @param text 通知内容
     */
    public void sendNotification(String text, Object... array) {
        this.sendMessage(new MessageFormatter(text).fill(array), NotificationType.INFORMATION);
    }

    /**
     * 推送错误通知
     *
     * @param text 通知内容
     */
    public void sendErrorNotification(String text, Object... array) {
        this.sendMessage(new MessageFormatter(text).fill(array), NotificationType.ERROR);
    }

    protected void sendMessage(String text, NotificationType type) {
        Project project = context.getActionEvent().getProject();
        if (project != null) {
            Notification notification = new Notification(ClassUtils.getPackageName(MavenFinder.class, 3), this.getName(), text, type);
            Notifications.Bus.notify(notification, project);
        }
    }

    public void sendNotification(String text, File file) {
        Project project = context.getActionEvent().getProject();
        if (project != null) {
            Notification notification = new Notification(ClassUtils.getPackageName(MavenFinder.class, 3), this.getName(), text, NotificationType.INFORMATION);
            notification.addAction(new NotificationAction("Open File") {

                @Override
                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                    if (file.exists()) {
                        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
                        if (virtualFile != null) {
                            FileEditorManager.getInstance(project).openFile(virtualFile, true); // 使用 IDE 的文件编辑器打开文件
                        }
                    }
                }
            });
            Notifications.Bus.notify(notification, project);
        }
    }

    /**
     * 返回插件名
     *
     * @return 插件名
     */
    public @NotNull String getName() {
        return this.getClass().getSimpleName();
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
     * 设置搜索输入框中的文本
     *
     * @param text 文本信息
     */
    public void setSearchFieldText(String text) {
        context.getSearchEverywhereUI().getSearchField().setText(text);
    }

    /**
     * 更新搜索结果下方：广告栏中的信息
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
        MavenSearchResult result = this.context.getPatternSearchResult();
        this.repaint(result);
    }

    /**
     * 使用参数指定的查询结果，渲染 UI 界面
     *
     * @param result 查询结果
     */
    public synchronized void repaint(MavenSearchResult result) {
        if (result == null) {
            log.warn("repaint fail, result is null!");
            return;
        }

        JBList<Object> JBList = this.context.getJBList();
        SearchListModel listModel = this.context.getJBListModel();
        listModel.clearMoreItems(); // 一定要先删除 more 按钮

        // 将查询结果转为导航记录
        java.util.List<MavenFinderNavigation> list = this.toNavigationList(result);

        // 将导航记录合并到数据模型中
        this.mergeNavigation(listModel, list);

        // 选中记录
        this.setSelection(JBList, listModel);

        // 设置 more 按钮
        listModel.setHasMore(this.context.getContributor(), result.getFoundNumber() > result.size());
        listModel.freezeElements();

        // 渲染 JBList
        try {
            JBList.repaint();
            JBList.revalidate();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        // 设置广告信息
        log.warn("repaint: " + JBList.getClass().getSimpleName() + ", size: " + listModel.getSize() + ", " + JBList.getModel().getSize() + ", " + listModel.isResultsExpired());
        String message = MavenFinderMessage.REMOTE_SEARCH_RESULT.fill(result.getFoundNumber(), result.size());
        this.setAdvertiser(message, MavenFinderIcon.BOTTOM);
    }

    private void updateComparator(SearchListModel listModel, Comparator comparator) {
        if (listModel.getClass().getSimpleName().equals("MixedSearchListModel")) {
            try {
                JavaDialectFactory.get().setField(listModel, "myElementsComparator", comparator);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * 选中记录
     *
     * @param jbList    组件
     * @param listModel 组件的数据模型
     */
    protected void setSelection(JBList<Object> jbList, SearchListModel listModel) {
        MavenFinderNavigationCatalog selectedItem = this.context.getSelectCatalog();
        if (selectedItem != null) {
            int selectedIndex = -1;
            for (int i = listModel.getSize() - 1; i >= 0; i--) {
                Object object = listModel.getElementAt(i);
                if (object instanceof MavenFinderNavigationCatalog) {
                    MavenFinderNavigationCatalog item = (MavenFinderNavigationCatalog) object;
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
    protected java.util.List<MavenFinderNavigation> toNavigationList(MavenSearchResult result) {
        java.util.List<MavenArtifact> list = result.getList();
        int size = list.size();
        java.util.List<MavenFinderNavigation> newList = new ArrayList<MavenFinderNavigation>(size);
        for (MavenArtifact artifact : list) {
            MavenFinderNavigationCatalog catalog = new MavenFinderNavigationCatalog(artifact);
            newList.add(catalog);

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();

            MavenSearchResult versionResult = this.getDatabase().select(groupId, artifactId);
            if (versionResult != null) {
                catalog.setIcon(MavenFinderIcon.LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (versionResult != null) {
                    catalog.setIcon(MavenFinderIcon.LEFT_UNFOLD);
                    for (MavenArtifact version : versionResult.getList()) {
                        MavenFinderNavigationItem navigation = new MavenFinderNavigationItem(version);
                        if (this.getLocalMavenRepository().exists(version)) {
                            navigation.setIcon(MavenFinderIcon.RIGHT_LOCAL);
                        }
                        newList.add(navigation);
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (this.getSearch().isSearching(groupId, artifactId)) {
                catalog.setIcon(MavenFinderIcon.LEFT_WAITING);
            }
        }
        return newList;
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
                } catch (Throwable e) { // 如果不能删除，则将导航记录清空，排序时放到最后
                    log.error(e.getLocalizedMessage(), e);
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
                        log.error(e.getLocalizedMessage(), e);
                        element.setPriority(Integer.MIN_VALUE); // 将权重设置为最小，比 More 元素（Priority=0）小
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
                MavenFinderFoundElementInfo elementInfo = new MavenFinderFoundElementInfo(navigation, this.context.getContributor());
                newList.add(elementInfo);
            } while (it.hasNext());

            this.updateComparator(listModel, new NavigationItemComparator());
            try {
                listModel.addElements(newList);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            } finally {
                this.updateComparator(listModel, SearchEverywhereFoundElementInfo.COMPARATOR.reversed());
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
