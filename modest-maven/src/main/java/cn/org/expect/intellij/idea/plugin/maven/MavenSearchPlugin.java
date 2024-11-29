package cn.org.expect.intellij.idea.plugin.maven;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.Map;

import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchRepaintJob;
import cn.org.expect.intellij.idea.plugin.maven.listener.MavenSearchPluginListener;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.concurrent.MavenSearchExtraJob;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.local.LocalRepositorySettings;
import cn.org.expect.maven.search.AbstractMavenSearch;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.maven.search.MavenSearchNotification;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.MessageFormatter;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportingSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public class MavenSearchPlugin extends AbstractMavenSearch implements Disposable {
    private final static Log log = LogFactory.getLog(MavenSearchPlugin.class);

    /** Idea查询对话框对象 */
    private final IdeaSearchUI ideaUI;

    private final MavenSearchPluginContext context;

    private final MavenSearchPluginContributor contributor;

    private final MavenSearchPluginSettings settings;

    private final MavenSearchPluginListener listener;

    public MavenSearchPlugin(MavenSearchPluginContext context) {
        super(MavenSearchPluginApplication.get());
        this.context = Ensure.notNull(context);
        this.ideaUI = new IdeaSearchUI();
        this.settings = this.getEasyContext().getBean(MavenSearchPluginSettings.class);
        this.contributor = new MavenSearchPluginContributor(this);
        this.listener = new MavenSearchPluginListener(this);
    }

    public MavenSearchPluginListener getSearchListener() {
        return listener;
    }

    public IdeaSearchUI getIdeaUI() {
        return this.ideaUI;
    }

    public MavenSearchPluginContext getContext() {
        return this.context;
    }

    public MavenSearchPluginSettings getSettings() {
        return this.settings;
    }

    public LocalRepositorySettings getLocalRepositorySettings() {
        MavenProjectsManager manager = MavenProjectsManager.getInstance(this.context.getActionEvent().getProject());
        MavenImportingSettings importingSettings = manager.getImportingSettings();

        LocalRepositorySettings localRepositorySettings = super.getLocalRepositorySettings();
        localRepositorySettings.setDownloadSourcesAutomatically(importingSettings.isDownloadSourcesAutomatically());
        localRepositorySettings.setDownloadDocsAutomatically(importingSettings.isDownloadDocsAutomatically());
        localRepositorySettings.setDownloadAnnotationsAutomatically(importingSettings.isDownloadAnnotationsAutomatically());

        if (log.isDebugEnabled()) {
            log.debug("{} sources: {}, docs: {}, annotations: {}", LocalRepositorySettings.class.getSimpleName(), localRepositorySettings.isDownloadSourcesAutomatically(), localRepositorySettings.isDownloadDocsAutomatically(), localRepositorySettings.isDownloadAnnotationsAutomatically());
        }
        return localRepositorySettings;
    }

    /**
     * 返回搜索贡献者
     *
     * @return 搜索贡献者
     */
    public MavenSearchPluginContributor getContributor() {
        return this.contributor;
    }

    public void asyncSearch() {
        String pattern = this.getIdeaUI().getSearchField().getText();
        this.asyncSearch(pattern, false);
    }

    public void asyncRefresh() {
        String pattern = this.context.getSearchText();
        this.asyncSearch(pattern, true);
    }

    public void asyncSearch(String pattern) {
        this.asyncSearch(pattern, false);
    }

    public void asyncSearch(String pattern, boolean delete) {
        if (StringUtils.isBlank(pattern)) {
            return;
        }

        this.context.setSelectNavigationHead(null);
        this.context.setSelectNavigationItem(null);

        // 更新等待信息与状态栏
        this.setProgress(MavenSearchMessage.get("maven.search.progress.text"));
        this.setStatusBar(MavenSearchAdvertiser.RUNNING, MavenSearchMessage.get("maven.search.pattern.text", StringUtils.escapeLineSeparator(pattern)));
        this.getInput().search(this, pattern, delete);
    }

    public void asyncSearch(String groupId, String artifactId) {
//        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId)) {
//            throw new UnsupportedOperationException(groupId + ":" + artifactId);
//        }

        String message = MavenSearchMessage.get("maven.search.extra.text", groupId, artifactId);
        this.setStatusBar(MavenSearchAdvertiser.RUNNING, message);
        this.execute(new MavenSearchExtraJob(groupId, artifactId));
    }

    public void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    public void sendNotification(MavenSearchNotification type, String text, Object... array) {
        String message = new MessageFormatter(text).fill(array);
        NotificationType notificationType = MavenSearchPluginUtils.toNotification(type);
        Project project = context.getActionEvent().getProject();
        if (project != null) {
            Notification notification = new Notification(this.getSettings().getId(), this.getSettings().getName(), message, notificationType);
            Notifications.Bus.notify(notification, project);
        }
    }

    public void sendNotification(MavenSearchNotification type, String text, String actionName, File file) {
        Project project = context.getActionEvent().getProject();
        if (project != null) {
            NotificationType notificationType = MavenSearchPluginUtils.toNotification(type);
            Notification notification = new Notification(this.getSettings().getId(), this.getSettings().getName(), text, notificationType);
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
     * 判断当前 Tab标签页 是否是自身
     *
     * @return 返回true表示是 false表示不是
     */
    public boolean isSelfTab() {
        if (this.getIdeaUI().getSearchEverywhereUI() == null) {
            return false;
        }

        String tabID = this.getIdeaUI().getSelectedTabID();
        return this.contributor.getSearchProviderId().equals(tabID);
    }

    /**
     * 判断当前 Tab标签页 是否是 All
     *
     * @return 返回true表示是 false表示不是
     */
    public boolean isAllTab() {
        if (this.getIdeaUI().getSearchEverywhereUI() == null) {
            return false;
        }

        String tabID = this.getIdeaUI().getSelectedTabID();
        return tabID.endsWith("." + MavenSearchPluginUtils.getAllTabName());
    }

    /**
     * 是否能执行查询
     *
     * @return 返回true表示支持查询 false表示不支持
     */
    public boolean canSearch() {
        if (this.getIdeaUI().getSearchEverywhereUI() == null) {
            return false;
        }

        String tabID = this.getIdeaUI().getSelectedTabID();
        return (this.getSettings().isUseAllTab() && tabID.endsWith("." + MavenSearchPluginUtils.getAllTabName())) || this.contributor.getSearchProviderId().equals(tabID);
    }

    /**
     * 在 Tab 页上添加提示信息
     */
    public void updateTabTooltip() {
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(this.context.getActionEvent().getProject());
        Map<String, String> map = JavaDialectFactory.get().getField(manager, "myTabsShortcutsMap");
        if (map != null) {
            String text = MavenSearchMessage.get("maven.search.tab.tooltip.text", MavenSearchPluginUtils.getShortcutText("pressed F2"));
            map.put(this.contributor.getSearchProviderId(), text);
        }
    }

    public void setStatusBar(MavenSearchAdvertiser type, String message) {
        if (this.isSelfTab()) {
            this.getIdeaUI().setStatusBar(type, message);
        } else { // 如果标签页不是自身，则将状态栏恢复到原来的样式
            this.getIdeaUI().setStatusBar(null, "");
        }
    }

    public void display(MavenSearchResult result) {
        this.aware(new MavenSearchRepaintJob(result)).run();
    }

    public void asyncDisplay() {
        this.execute(new MavenSearchRepaintJob(this.context.getSearchResult()));
    }

    public void setProgress(String message) {
        if (this.isSelfTab()) {
            this.getIdeaUI().getDisplay().setProgress(message);
        }
    }

    public void dispose() {
    }
}
