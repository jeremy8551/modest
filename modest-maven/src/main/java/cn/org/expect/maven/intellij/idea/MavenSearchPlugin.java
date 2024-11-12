package cn.org.expect.maven.intellij.idea;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.intellij.idea.navigation.MavenFoundElementInfo;
import cn.org.expect.maven.intellij.idea.navigation.MavenFoundElementInfoComparator;
import cn.org.expect.maven.intellij.idea.navigation.MavenSearchNavigation;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigation;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationHead;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationItem;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationResultSet;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.AbstractMavenSearch;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.maven.search.MavenSearchNotification;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.MessageFormatter;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import com.intellij.util.Alarm;
import com.intellij.util.ui.Advertiser;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPlugin extends AbstractMavenSearch implements Disposable {
    private final static Log log = LogFactory.getLog(MavenSearchPlugin.class);

    private final MavenSearchPluginContext context;

    private final Alarm rebuildListAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);

    public MavenSearchPlugin(MavenSearchPluginContext context) {
        super(RepositoryConfigFactory.getInstance(context.getActionEvent()));
        this.context = Ensure.notNull(context);
    }

    @Override
    public MavenSearchPluginContext getContext() {
        return this.context;
    }

    /**
     * 返回搜索贡献者
     *
     * @return 搜索贡献者
     */
    public MavenPluginContributor getContributor() {
        MavenPluginContributor contributor = new MavenPluginContributor(this);  // 创建 Idea 搜索的贡献者
        this.context.setContributor(contributor);
        return contributor;
    }

    @Override
    public void asyncSearch(String pattern) {
        this.context.setSearchText(pattern);
        this.context.setSelectedNavigation(null);
        this.getInputSearch().search(this, pattern);
    }

    @Override
    public void asyncSearch(String groupId, String artifactId) {
        this.getServiceSearch().searchExtra(this, groupId, artifactId);
    }

    @Override
    public void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    @Override
    public void sendNotification(MavenSearchNotification type, String text, Object... array) {
        String message = new MessageFormatter(text).fill(array);
        NotificationType notificationType = IdeaUtils.toNotification(type);
        Project project = context.getActionEvent().getProject();
        if (project != null) {
            Notification notification = new Notification(this.getGroupId(), this.getName(), message, notificationType);
            Notifications.Bus.notify(notification, project);
        }
    }

    @Override
    public void sendNotification(MavenSearchNotification type, String text, String actionName, File file) {
        Project project = context.getActionEvent().getProject();
        if (project != null) {
            NotificationType notificationType = IdeaUtils.toNotification(type);
            Notification notification = new Notification(this.getGroupId(), this.getName(), text, notificationType);
            notification.addAction(new NotificationAction(actionName) {

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

    private String getGroupId() {
        return MavenSearchPlugin.class.getPackage().getName();
    }

    /**
     * 返回插件名
     *
     * @return 插件名
     */
    public @NotNull String getName() {
        return "Maven+ Plugin";
    }

    /**
     * 判断当前标签页是否满足条件
     *
     * @return 返回true表示标签页不符合 false表示符合
     */
    public boolean notMavenSearchTab() {
        SearchEverywhereUI ui = this.context.getSearchEverywhereUI();
        if (ui == null) {
            return false;
        }

        String selectedTabID = ui.getSelectedTabID();
        return !this.context.getContributor().getSearchProviderId().equals(selectedTabID);
    }

    @Override
    public void setSearchFieldText(String text) {
        context.getSearchEverywhereUI().getSearchField().setText(text);
    }

    @Override
    public void setStatusbarText(MavenSearchAdvertiser type, String message) {
        if (this.notMavenSearchTab()) {
            return;
        }

        Advertiser advertiser = this.context.getAdvertiser();
        if (advertiser == null) {
            return;
        }

        String fontColor = MavenSearchAdvertiser.ERROR == type ? "red" : "orange";
        Icon icon = IdeaUtils.getIcon(type);

        try {
            JLabel myTextPanel = JavaDialectFactory.get().getField(advertiser, "myTextPanel");
            myTextPanel.setText(new MessageFormatter("<html><span style='color:{};'>{}</span></html>").fill(fontColor, message));
            myTextPanel.setIcon(icon);
            myTextPanel.repaint();

            JLabel myNextLabel = JavaDialectFactory.get().getField(advertiser, "myNextLabel");
            myNextLabel.setText(null);
            myNextLabel.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void setProgressText(String message) {
        if (this.notMavenSearchTab()) {
            return;
        }

        JBList<Object> JBList = this.context.getJBList();
        if (JBList != null) {
            JBList.setEmptyText(message);
        }
    }

    @Override
    public synchronized void clearSearchResultUI() {
        SearchListModel listModel = this.context.getJBListModel();
        for (int i = listModel.getSize() - 1; i >= 0; i--) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            Object element = info.getElement();

            if (!(info instanceof MavenFoundElementInfo) && element instanceof MavenSearchNavigation) {
                try {
                    listModel.removeElement(element, info.getContributor());
                } catch (Throwable e) { // 如果不能删除，则将导航记录清空，排序时放到最后
                    log.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    @Override
    public synchronized void repaintSearchResult() {
        MavenSearchResult result = this.context.getSearchResult();
        this.repaintSearchResult(result);
    }

    @Override
    public synchronized void repaintSearchResult(MavenSearchResult result) {
        if (result == null) {
            log.warn("repaint fail, result is null!");
            return;
        }

        JBList<Object> JBList = this.context.getJBList();
        SearchListModel listModel = this.context.getJBListModel();
        listModel.clearMoreItems(); // 一定要先删除 more 按钮

        // 将查询结果转为导航记录
        java.util.List<MavenSearchNavigation> list = this.toNavigationList(result);

        // 将导航记录合并到数据模型中
        this.mergeNavigation(listModel, list);

        // 选中记录
        this.setSelection(JBList, listModel);

        // 设置 more 按钮
        listModel.setHasMore(this.context.getContributor(), result.getFoundNumber() > result.size());
        listModel.freezeElements();

        // 渲染 JBList
        try {
            JBList.revalidate();
            JBList.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("repaint, size: {}, {}", listModel.getSize(), JBList.getModel().getSize());
        }

        // 设置广告信息
        String message = MavenSearchMessage.get("maven.search.status.text", result.getFoundNumber(), result.size());
        this.setStatusbarText(MavenSearchAdvertiser.NORMAL, message);
    }

    @Override
    public synchronized void repaintMoreSearchResult(MavenSearchResult result) {
        Ensure.notNull(result);

        SearchNavigationResultSet resultSet = this.toNavigationResultSet(result);
        this.context.setNavigationResultSet(resultSet);

        String message = MavenSearchMessage.get("maven.search.status.text", result.getFoundNumber(), result.size());
        this.setStatusbarText(MavenSearchAdvertiser.NORMAL, message);

        if (log.isDebugEnabled()) {
            log.debug("repaintMoreSearchResult(), rebuildList, size: {} ", resultSet.size());
        }

        // 刷新 JList 界面
        this.runEdtThread(() -> context.getContributor().getRebuildListTask().run(), 100);
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
        SearchNavigationHead selectedItem = this.context.getSelectedNavigation();
        if (selectedItem != null) {
            int selectedIndex = -1;
            for (int i = listModel.getSize() - 1; i >= 0; i--) {
                Object object = listModel.getElementAt(i);
                if (object instanceof SearchNavigationHead) {
                    SearchNavigationHead item = (SearchNavigationHead) object;
                    if (selectedItem.getArtifact().equals(item.getArtifact()) && item.getArtifact().isUnfold()) {
                        selectedIndex = i;

                        // 设置左侧等待图标
                        MavenArtifact artifact = selectedItem.getArtifact();
                        if (artifact.isUnfold() && this.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId()) == null) {
                            item.setIcon(MavenPluginIcon.LEFT_WAITING);
                        }
                        break;
                    }
                }
            }

            if (selectedIndex == -1) {
                jbList.clearSelection();
            } else {
                jbList.setSelectedIndex(selectedIndex);
                jbList.ensureIndexIsVisible(selectedIndex);
            }
        }
    }

    /**
     * 将查询结果转为导航记录
     *
     * @param result 查询结果
     * @return 导航记录的集合
     */
    protected java.util.List<MavenSearchNavigation> toNavigationList(MavenSearchResult result) {
        java.util.List<MavenArtifact> list = result.getList();
        int size = list.size();
        java.util.List<MavenSearchNavigation> newList = new ArrayList<MavenSearchNavigation>(size);
        for (MavenArtifact artifact : list) {
            SearchNavigationHead head = new SearchNavigationHead(artifact);
            newList.add(head);

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();

            MavenSearchResult itemResult = this.getDatabase().select(groupId, artifactId);
            if (itemResult != null) {
                head.setIcon(MavenPluginIcon.LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (itemResult != null) {
                    head.setIcon(MavenPluginIcon.LEFT_UNFOLD);
                    for (MavenArtifact itemArtifact : itemResult.getList()) {
                        SearchNavigationItem item = new SearchNavigationItem(itemArtifact);
                        if (this.getLocalRepository().exists(itemArtifact)) {
                            item.setIcon(MavenPluginIcon.RIGHT_LOCAL);
                        }
                        newList.add(item);
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (this.getServiceSearch().isSearching(groupId, artifactId)) {
                head.setIcon(MavenPluginIcon.LEFT_WAITING);
            }
        }
        return newList;
    }

    /**
     * 将查询结果转为导航记录，目标是提供给 {@link MavenPluginChooseContributor} 使用
     *
     * @param result 查询结果
     * @return 导航记录
     */
    protected SearchNavigationResultSet toNavigationResultSet(MavenSearchResult result) {
        java.util.List<MavenArtifact> list = result.getList();
        java.util.List<SearchNavigation> newList = new ArrayList<SearchNavigation>(list.size());
        for (MavenArtifact artifact : list) {
            SearchNavigationHead head = new SearchNavigationHead(artifact);
            List<SearchNavigationItem> items = new ArrayList<>();

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            MavenSearchResult itemResult = this.getDatabase().select(groupId, artifactId);
            if (itemResult != null) {
                head.setIcon(MavenPluginIcon.LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (itemResult != null) {
                    head.setIcon(MavenPluginIcon.LEFT_UNFOLD);
                    for (MavenArtifact itemArtifact : itemResult.getList()) {
                        SearchNavigationItem item = new SearchNavigationItem(itemArtifact);
                        if (this.getLocalRepository().exists(itemArtifact)) {
                            item.setIcon(MavenPluginIcon.RIGHT_LOCAL);
                        }
                        items.add(item);
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (this.getServiceSearch().isSearching(groupId, artifactId)) {
                head.setIcon(MavenPluginIcon.LEFT_WAITING);
            }

            newList.add(new SearchNavigation(head, items));
        }

        return new SearchNavigationResultSet(newList);
    }

    /**
     * 将导航记录添加到数据模型中
     *
     * @param listModel 数据模型
     * @param list      导航记录
     */
    protected void mergeNavigation(SearchListModel listModel, java.util.List<MavenSearchNavigation> list) {
        // 删除（自动添加的）导航记录
        for (int i = listModel.getSize() - 1; i >= 0; i--) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            Object element = info.getElement();

            if (!(info instanceof MavenFoundElementInfo) && element instanceof MavenSearchNavigation) {
                try {
                    listModel.removeElement(element, info.getContributor());
                } catch (Throwable e) { // 如果不能删除，则将导航记录清空，排序时放到最后
                    log.error(e.getLocalizedMessage(), e);
                }
            }
        }

        // 添加导航记录
        int i = 0;
        Iterator<MavenSearchNavigation> it = list.iterator();
        for (; i < listModel.getSize(); i++) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            if (info instanceof MavenFoundElementInfo) {
                MavenFoundElementInfo element = (MavenFoundElementInfo) info;
                if (it.hasNext()) { // 向 ListModel 添加记录
                    MavenSearchNavigation item = it.next();
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
            if (info instanceof MavenFoundElementInfo) {
                MavenFoundElementInfo element = (MavenFoundElementInfo) info;
                MavenSearchNavigation item = element.getElement();
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
            java.util.List<MavenFoundElementInfo> newList = new ArrayList<MavenFoundElementInfo>(list.size());
            do {
                MavenSearchNavigation navigation = it.next();
                MavenFoundElementInfo elementInfo = new MavenFoundElementInfo(navigation, this.context.getContributor());
                newList.add(elementInfo);
            } while (it.hasNext());

            this.updateComparator(listModel, new MavenFoundElementInfoComparator());
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
            return StringUtils.trimBlank(editor.getSelectionModel().getSelectedText());
        } else {
            return null;
        }
    }

    /**
     * 使用官方的 EDT 线程执行 swing 相关任务
     */
    public void runEdtThread(Runnable task, long delayMillis) {
        if (!this.rebuildListAlarm.isDisposed() && this.rebuildListAlarm.getActiveRequestCount() == 0) {
            this.rebuildListAlarm.addRequest(task, delayMillis);
        }
    }

    @Override
    public void dispose() {
    }
}
