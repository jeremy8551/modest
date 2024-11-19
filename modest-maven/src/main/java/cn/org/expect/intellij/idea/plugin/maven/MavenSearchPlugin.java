package cn.org.expect.intellij.idea.plugin.maven;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenFoundElementInfoComparator;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationItem;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationResultSet;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.concurrent.MavenSearchDownloadJob;
import cn.org.expect.maven.concurrent.MavenSearchEDTJob;
import cn.org.expect.maven.concurrent.MavenSearchExtraJob;
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
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPlugin extends AbstractMavenSearch implements Disposable {
    private final static Log log = LogFactory.getLog(MavenSearchPlugin.class);

    /** Idea查询对话框对象 */
    private final IdeaSearchUI ideaUI;

    private final MavenSearchPluginContext context;

    private final MavenSearchPluginContributor contributor;

    public MavenSearchPlugin(MavenSearchPluginContext context) {
        super(context.getRemoteRepositoryName(), DefaultLocalRepositoryConfig.getInstance(context.getActionEvent()));
        this.ideaUI = new IdeaSearchUI();
        this.context = Ensure.notNull(context);
        this.contributor = new MavenSearchPluginContributor(this);
    }

    public IdeaSearchUI getIdeaUI() {
        return this.ideaUI;
    }

    public MavenSearchPluginContext getContext() {
        return this.context;
    }

    /**
     * 返回搜索贡献者
     *
     * @return 搜索贡献者
     */
    public MavenSearchPluginContributor getContributor() {
        return this.contributor;
    }

    public void asyncSearch(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return;
        }

        this.context.setSearchText(pattern);
        this.context.setSelectNavigationHead(null);
        this.context.setSelectNavigationItem(null);

        this.setProgressText(MavenSearchMessage.get("maven.search.progress.text"));
        this.setStatusbarText(MavenSearchAdvertiser.RUNNING, MavenSearchMessage.get("maven.search.pattern.text", StringUtils.escapeLineSeparator(pattern)));
        this.getInput().search(this, pattern);
    }

    public void asyncSearch(String groupId, String artifactId) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId)) {
            throw new UnsupportedOperationException(groupId + ":" + artifactId);
        }

        String message = MavenSearchMessage.get("maven.search.extra.text", groupId, artifactId);
        this.setStatusbarText(MavenSearchAdvertiser.RUNNING, message);
        this.execute(new MavenSearchExtraJob(groupId, artifactId));
    }

    public void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    public void sendNotification(MavenSearchNotification type, String text, Object... array) {
        String message = new MessageFormatter(text).fill(array);
        NotificationType notificationType = MavenSearchUtils.toNotification(type);
        Project project = context.getActionEvent().getProject();
        if (project != null) {
            Notification notification = new Notification(this.getGroupId(), this.getName(), message, notificationType);
            Notifications.Bus.notify(notification, project);
        }
    }

    public void sendNotification(MavenSearchNotification type, String text, String actionName, File file) {
        Project project = context.getActionEvent().getProject();
        if (project != null) {
            NotificationType notificationType = MavenSearchUtils.toNotification(type);
            Notification notification = new Notification(this.getGroupId(), this.getName(), text, notificationType);
            notification.addAction(new NotificationAction(actionName) {

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
     * 判断当前标签页是否满足条件
     *
     * @return 返回true表示标签页不符合 false表示符合
     */
    public boolean notMavenSearchTab() {
        if (this.getIdeaUI().getSearchEverywhereUI() == null) {
            return false;
        }

        String selectedTabID = this.getIdeaUI().getSelectedTabID();
        return !this.contributor.getSearchProviderId().equals(selectedTabID);
    }

    /**
     * 在 Tab 页上添加提示信息
     */
    public void updateTabTooltip() {
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(this.context.getActionEvent().getProject());
        Map<String, String> map = JavaDialectFactory.get().getField(manager, "myTabsShortcutsMap");
        if (map != null) {
            String text = MavenSearchMessage.get("maven.search.tab.tooltip.text", MavenSearchUtils.getShortcutText("pressed F2"));
            map.put(this.contributor.getSearchProviderId(), text);
        }
    }

    public void setSearchFieldText(String text) {
        this.getIdeaUI().getSearchField().setText(text);
    }

    public void setStatusbarText(MavenSearchAdvertiser type, String message) {
        if (this.notMavenSearchTab()) {
            return;
        }

        this.getIdeaUI().setStatusbarText(type, message);
    }

    public void setProgressText(String message) {
        if (this.notMavenSearchTab()) {
            return;
        }

        this.getIdeaUI().getJBList().setEmptyText(message);
    }

    /**
     * 重新执行查询
     */
    public void repeat() {
        String pattern = context.getSearchText();
        if (StringUtils.isNotBlank(pattern)) {
            this.getDatabase().delete(pattern);
            this.asyncSearch(cn.org.expect.maven.search.MavenSearchUtils.parse(pattern));
        }
    }

    public synchronized void clearSearchResult() {
        SearchListModel listModel = this.getIdeaUI().getListModel();
        for (int i = listModel.getSize() - 1; i >= 0; i--) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            Object element = info.getElement();

            if (element instanceof MavenSearchNavigation) {
                try {
                    listModel.removeElement(element, info.getContributor());
                } catch (Throwable e) { // 如果不能删除，则将导航记录清空，排序时放到最后
                    log.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    public synchronized void showSearchResult() {
        MavenSearchResult result = this.context.getSearchResult();
        this.showSearchResult(result);
    }

    public void showSearchResult(MavenSearchResult result) {
        this.execute(new MavenSearchEDTJob(() -> this.repaintSearchResult(result)));
    }

    protected synchronized void repaintSearchResult(MavenSearchResult result) {
        if (result == null) {
            log.warn("repaint fail, result is null!");
            return;
        }

        // TODO 需要与下面的方法合并
        SearchNavigationResultSet navigationResult = this.toNavigationResult(result);
        this.context.setNavigationResultSet(navigationResult);

        JBList<Object> JBList = this.getIdeaUI().getJBList();
        SearchListModel listModel = this.getIdeaUI().getListModel();

        // 一定要先删除 more 按钮
        listModel.clearMoreItems();

        // 将查询结果转为导航记录
        java.util.List<SearchEverywhereFoundElementInfo> list = this.toNavigationList(result);

        // 将导航记录合并到数据模型中
        this.mergeNavigation(listModel, list);

        // 设置选中记录
        this.setSelectNavigation(JBList, listModel);

        // 设置 more 按钮
        try {
            listModel.setHasMore(this.contributor, result.getFoundNumber() > result.size());
            listModel.freezeElements();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        // 渲染 JBList
        try {
            JBList.revalidate();
            JBList.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("repaintSearchResult, size: {}, {}", listModel.getSize(), JBList.getModel().getSize());
        }

        // 设置广告信息
        this.setStatusbarText(MavenSearchAdvertiser.NORMAL, MavenSearchMessage.get("maven.search.status.text", result.getFoundNumber(), result.size()));
    }

    /**
     * 选中记录
     *
     * @param JBList    组件
     * @param listModel 组件的数据模型
     */
    protected void setSelectNavigation(JBList<Object> JBList, SearchListModel listModel) {
        int selectedIndex = -1;

        SearchNavigationHead selectHead = this.context.getSelectNavigationHead();
        if (selectHead != null) {
            for (int i = 0, size = listModel.getSize(); i < size; i++) {
                Object object = listModel.getElementAt(i);
                if (object instanceof SearchNavigationHead) {
                    SearchNavigationHead head = (SearchNavigationHead) object;
                    if (selectHead.getArtifact().equals(head.getArtifact()) && head.getArtifact().isUnfold()) {
                        MavenArtifact artifact = selectHead.getArtifact();
                        if (artifact.isUnfold() && this.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId()) == null) {
                            head.setIcon(MavenSearchPluginIcon.LEFT_WAITING); // 设置左侧等待图标
                        }

                        selectedIndex = i;
                        break;
                    }
                }
            }
        }

        SearchNavigationItem selectItem = this.context.getSelectNavigationItem();
        if (selectItem != null) {
            for (int i = 0, size = listModel.getSize(); i < size; i++) {
                Object object = listModel.getElementAt(i);
                if (object instanceof SearchNavigationItem) {
                    SearchNavigationItem item = (SearchNavigationItem) object;
                    if (selectItem.getArtifact().equals(item.getArtifact())) {
                        selectedIndex = i;
                        break;
                    }
                }
            }
        }

        if (selectedIndex == -1) {
            JBList.clearSelection();
        } else {
            JBList.setSelectedIndex(selectedIndex);
            JBList.ensureIndexIsVisible(selectedIndex);
        }
    }

    /**
     * 将查询结果转为导航记录
     *
     * @param result 查询结果
     * @return 导航记录的集合
     */
    protected java.util.List<SearchEverywhereFoundElementInfo> toNavigationList(MavenSearchResult result) {
        java.util.List<MavenArtifact> list = result.getList();
        int size = list.size();
        java.util.List<SearchEverywhereFoundElementInfo> newList = new ArrayList<SearchEverywhereFoundElementInfo>(size);
        for (MavenArtifact artifact : list) {
            SearchNavigationHead head = new SearchNavigationHead(artifact);
            newList.add(new SearchEverywhereFoundElementInfo(head, MavenSearchNavigation.PRIORITY, this.contributor));

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();

            MavenSearchResult itemResult = this.getDatabase().select(groupId, artifactId);
            if (itemResult != null) {
                head.setIcon(MavenSearchPluginIcon.LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (itemResult != null) {
                    head.setIcon(MavenSearchPluginIcon.LEFT_UNFOLD);
                    for (MavenArtifact itemArtifact : itemResult.getList()) {
                        SearchNavigationItem item = new SearchNavigationItem(itemArtifact, this.getLocalRepository().getJarfile(itemArtifact));
                        if (this.getService().isRunning(MavenSearchDownloadJob.class, job -> job.getArtifact().equals(itemArtifact))) { // 正在下载
                            item.setIcon(MavenSearchPluginIcon.RIGHT_DOWNLOAD);
                        } else if (this.getLocalRepository().exists(itemArtifact)) {
                            item.setIcon(MavenSearchPluginIcon.RIGHT_LOCAL);
                        }
                        newList.add(new SearchEverywhereFoundElementInfo(item, MavenSearchNavigation.PRIORITY, this.contributor));
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (this.getService().isRunning(MavenSearchExtraJob.class, job -> groupId.equals(job.getGroupId()) && artifactId.equals(job.getArtifactId()))) {
                head.setIcon(MavenSearchPluginIcon.LEFT_WAITING);
            }
        }
        return newList;
    }

    /**
     * 将查询结果转为导航记录，目标是提供给 {@link MavenSearchPluginChooseContributor} 使用
     *
     * @param result 查询结果
     * @return 导航记录
     */
    protected SearchNavigationResultSet toNavigationResult(MavenSearchResult result) {
        java.util.List<MavenArtifact> list = result.getList();
        java.util.List<SearchNavigation> newList = new ArrayList<SearchNavigation>(list.size());
        for (MavenArtifact artifact : list) {
            SearchNavigationHead head = new SearchNavigationHead(artifact);
            List<SearchNavigationItem> items = new ArrayList<>();

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            MavenSearchResult itemResult = this.getDatabase().select(groupId, artifactId);
            if (itemResult != null) {
                head.setIcon(MavenSearchPluginIcon.LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (itemResult != null) {
                    head.setIcon(MavenSearchPluginIcon.LEFT_UNFOLD);
                    for (MavenArtifact itemArtifact : itemResult.getList()) {
                        SearchNavigationItem item = new SearchNavigationItem(itemArtifact, this.getLocalRepository().getJarfile(itemArtifact));
                        if (this.getLocalRepository().exists(itemArtifact)) {
                            item.setIcon(MavenSearchPluginIcon.RIGHT_LOCAL);
                        }
                        items.add(item);
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (this.getService().isRunning(MavenSearchExtraJob.class, job -> groupId.equals(job.getGroupId()) && artifactId.equals(job.getArtifactId()))) {
                head.setIcon(MavenSearchPluginIcon.LEFT_WAITING);
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
    protected void mergeNavigation(SearchListModel listModel, java.util.List<SearchEverywhereFoundElementInfo> list) {
        List<SearchEverywhereFoundElementInfo> all = new ArrayList<>(listModel.getSize() + list.size());
        boolean addAll = true;

        // 保存其他搜索类别的记录
        for (int i = 0; i < listModel.getSize(); i++) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            Object element = info.getElement();
            if (element instanceof MavenSearchNavigation) {
                if (addAll) {
                    all.addAll(list); // 将查询结果合并到 all 集合中
                    addAll = false;
                }
            } else {
                all.add(info);
            }
        }
        if (addAll) {
            all.addAll(list);
        }

        this.updateComparator(listModel, new MavenFoundElementInfoComparator());
        try {
            listModel.clear(); // 清空所有数据
            listModel.addElements(all);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            this.updateComparator(listModel, SearchEverywhereFoundElementInfo.COMPARATOR.reversed());
        }
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

    public void dispose() {
    }
}
