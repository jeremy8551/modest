package cn.org.expect.maven.intellij.idea;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.maven.concurrent.EDTJob;
import cn.org.expect.maven.concurrent.MavenSearchEDTJob;
import cn.org.expect.maven.concurrent.MavenSearchJob;
import cn.org.expect.maven.concurrent.MavenSearchMoreJob;
import cn.org.expect.maven.concurrent.MavenSearchdDownloadJob;
import cn.org.expect.maven.intellij.idea.listener.InputFieldListener;
import cn.org.expect.maven.intellij.idea.listener.SearchListener;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationItem;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.maven.search.MavenSearchNotification;
import cn.org.expect.maven.search.MavenSearchUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.ui.components.JBList;
import com.intellij.util.concurrency.EdtExecutorService;
import com.intellij.util.ui.Advertiser;

public class MavenSearchPluginJob extends MavenSearchJob implements EDTJob {

    public MavenSearchPluginJob() {
        super();
    }

    public int execute() {
        if (log.isInfoEnabled()) {
            log.info(MavenSearchMessage.get("maven.search.thread.start", this.getName()));
        }

        MavenSearchPlugin plugin = (MavenSearchPlugin) this.getSearch();
        MavenSearchPluginContext context = plugin.getContext(); // 上下文信息
        this.loadComponent(plugin); // 加载 UI 组件
        context.setLoadStatus(true); // 设置加载完成标志
        this.setPopupMenuUI(plugin); // 加载弹出菜单
        this.processEditorSelectText(plugin);

        if (log.isInfoEnabled()) {
            log.info(MavenSearchMessage.get("maven.search.thread.finish", this.getName()));
        }
        return 0;
    }

    /**
     * 将编辑器中选中的文本，复制到 Tab 页的输入框中
     *
     * @param plugin 搜索接口
     */
    private void processEditorSelectText(MavenSearchPlugin plugin) {
        MavenSearchPluginContext context = plugin.getContext();
        this.waitFor(context.getProgressIndicator(), 3000); // 等待 idea 默认的搜索功能执行完毕
        Editor editor = context.getActionEvent().getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            String editorSelectText = StringUtils.trimBlank(editor.getSelectionModel().getSelectedText());
            if (StringUtils.isNotBlank(editorSelectText)) {
                // 只能使用 EdtExecutorService.getInstance() 不能递归调用 plugin.execute() 方法
                EdtExecutorService.getInstance().execute(new MavenSearchEDTJob(() -> {

                    // 编辑器中选中的文本
                    String pattern = MavenSearchUtils.parse(editorSelectText);
                    if (log.isDebugEnabled()) {
                        log.debug("Idea editor selected text: {} --> {}", editorSelectText, pattern);
                    }

                    // 复制选中的文本到搜索栏
                    plugin.setSearchFieldText(pattern);

                    // 自动切换 Tab 页
                    if (context.isAutoSwitchTab() && MavenSearchUtils.isXML(editorSelectText)) {
                        context.getSearchEverywhereUI().switchToTab(plugin.getContributor().getSearchProviderId());
                    }
                }));
            }
        }
    }

    /**
     * 等待 SearchEverywhereUI 组件渲染完毕
     *
     * @param timeout 超时时间，单位：毫秒
     */
    protected void waitFor(ProgressIndicator progress, long timeout) {
        if (progress != null) {
            long startMillis = System.currentTimeMillis();
            while (progress.isRunning() && !progress.isCanceled() && System.currentTimeMillis() - startMillis <= timeout) {
                Dates.sleep(100);
            }
        }
    }

    /**
     * 检测Idea的UI组件
     *
     * @param plugin 事件
     */
    protected void loadComponent(MavenSearchPlugin plugin) {
        MavenSearchPluginContext context = plugin.getContext();
        AnActionEvent event = context.getActionEvent();
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
        long startMillis = System.currentTimeMillis();
        while (!manager.isShown()) { // 等待 SearchEverywhere 显示
            if (System.currentTimeMillis() - startMillis >= 3000) {
                break;
            } else {
                Dates.sleep(100);
            }
        }

        // 得到当前已显示的 SearchEverywhere 对象
        SearchEverywhereUI ui = manager.getCurrentlyShownUI();
        context.setSearchEverywhereUI(ui);
        ui.addSearchListener(new SearchListener(plugin));
        plugin.setService(JavaDialectFactory.get().getField(ui, "rebuildListAlarm"));

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
            Advertiser advertiser = JavaDialectFactory.get().getField(ui, "myHintLabel");
            context.setAdvertiser(advertiser);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            JTextField searchField = ui.getSearchField();
            context.setSearchField(searchField);
            searchField.addKeyListener(new InputFieldListener(plugin)); // 打开搜索对话框后，点击 Shift 快捷键自动切换 Tab 页
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    protected void setPopupMenuUI(MavenSearchPlugin plugin) {
        MavenSearchPluginContext context = plugin.getContext();
        JPopupMenu listPopupMenu = new JPopupMenu();
        JMenuItem copyMaven = new JMenuItem(MavenSearchMessage.get("maven.search.btn.copy.maven.dependency.text")); // 复制 Maven 依赖
        JMenuItem copyGradle = new JMenuItem(MavenSearchMessage.get("maven.search.btn.copy.gradle.dependency.text")); // 复制 Gradle 依赖
        JMenuItem openInBrowser = new JMenuItem(MavenSearchMessage.get("maven.search.btn.open.in.browser.text")); // 在浏览器中打开
        JMenuItem openFileSystem = new JMenuItem(MavenSearchMessage.get("maven.search.btn.open.in.filesystem.text")); // 打开本地仓库目录
        JMenuItem downloadFile = new JMenuItem(MavenSearchMessage.get("maven.search.btn.download.local.repository.text")); // 下载按钮
        JMenuItem cancelDownload = new JMenuItem(MavenSearchMessage.get("maven.search.btn.cancel.download.local.repository.text")); // 取消下载按钮
        JMenuItem deleteFile = new JMenuItem(MavenSearchMessage.get("maven.search.btn.delete.local.repository.text")); // 删除本地仓库中的文件

        // 必须要有以下菜单
        listPopupMenu.add(copyMaven);
        listPopupMenu.add(copyGradle);
        listPopupMenu.add(openInBrowser);

        JPopupMenu itemPopupMenu = new JPopupMenu();
        JMenuItem repeat = new JMenuItem(MavenSearchMessage.get("maven.search.btn.refresh.query.text"));
        JMenuItem clearCache = new JMenuItem(MavenSearchMessage.get("maven.search.btn.clear.cache.text"));
        itemPopupMenu.add(repeat);
        itemPopupMenu.add(clearCache);

        JBList<Object> JBList = context.getJBList();
        SearchListModel listModel = context.getJBListModel();

        // 复制 Maven 依赖
        copyMaven.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            String text = "";
            text += "<groupId>";
            text += selectItem.getArtifact().getGroupId();
            text += "</groupId>\n";
            text += "<artifactId>";
            text += selectItem.getArtifact().getArtifactId();
            text += "</artifactId>\n";
            text += "<version>";
            text += selectItem.getArtifact().getVersion();
            text += "</version>\n";

            plugin.copyToClipboard(text);
            plugin.sendNotification(MavenSearchNotification.NORMAL, copyMaven.getText());
        });

        // 复制 Gradle 依赖
        copyGradle.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            String text = "";
            text += "implementation '";
            text += selectItem.getArtifact().getGroupId();
            text += ":";
            text += selectItem.getArtifact().getArtifactId();
            text += ":";
            text += selectItem.getArtifact().getVersion();
            text += "'";

            plugin.copyToClipboard(text);
            plugin.sendNotification(MavenSearchNotification.NORMAL, copyGradle.getText());
        });

        // 在浏览器中打开
        openInBrowser.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            MavenArtifact artifact = selectItem.getArtifact();
            List<String> list = new ArrayList<>();
            list.add(plugin.getRemoteRepository().getAddress());
            StringUtils.split(artifact.getGroupId(), '.', list);
            list.add(artifact.getArtifactId());
            list.add(artifact.getVersion());
            String url = NetUtils.joinUri(list.toArray(new String[0]));
            BrowserUtil.browse(url);
        });

        // 打开文件系统目录
        openFileSystem.addActionListener(e -> {
            String filepath = plugin.getLocalRepository().getAddress();
            if (StringUtils.isBlank(filepath)) {
                return;
            }

            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            MavenArtifact artifact = selectItem.getArtifact();
            List<String> list = new ArrayList<>();
            list.add(filepath);
            StringUtils.split(artifact.getGroupId(), '.', list);
            list.add(artifact.getArtifactId());
            list.add(artifact.getVersion());
            BrowserUtil.browse(new File(FileUtils.joinPath(list.toArray(new String[0]))));
        });

        // 下载文件
        downloadFile.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            MavenArtifact artifact = selectItem.getArtifact();
            String message = MavenSearchMessage.get("maven.search.download.url", artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion());
            plugin.setStatusbarText(MavenSearchAdvertiser.RUNNING, message);
            plugin.execute(new MavenSearchdDownloadJob(artifact));
            plugin.showSearchResult();
        });

        // 取消下载
        cancelDownload.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            MavenArtifact artifact = selectItem.getArtifact();
            plugin.getService().terminate(MavenSearchdDownloadJob.class, job -> job.getArtifact().equals(artifact));
        });

        // 删除文件
        deleteFile.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            MavenArtifact artifact = selectItem.getArtifact();
            List<String> list = new ArrayList<>();
            list.add(plugin.getLocalRepository().getAddress());
            StringUtils.split(artifact.getGroupId(), '.', list);
            list.add(artifact.getArtifactId());
            list.add(artifact.getVersion());
            File dir = new File(FileUtils.joinPath(list.toArray(new String[0])));

            if (dir.exists()) {
                if (log.isDebugEnabled()) {
                    log.debug("delete local repository {} ..", dir.getAbsolutePath());
                }
                FileUtils.delete(dir);
                plugin.showSearchResult();
            }
        });

        // 重新执行查询
        repeat.addActionListener(e -> {
            plugin.repeat();
            plugin.sendNotification(MavenSearchNotification.NORMAL, repeat.getText());
        });

        // 清空所有缓存
        clearCache.addActionListener(e -> {
            plugin.getDatabase().clear();
            plugin.clearSearchResult(); // 刷新一个空结果
            plugin.setProgressText("");
            plugin.setStatusbarText(null, "");
            plugin.setSearchFieldText("");
            plugin.getContext().setNavigationResultSet(null);
            plugin.sendNotification(MavenSearchNotification.NORMAL, clearCache.getText());
        });

        // 监听鼠标事件
        JBList.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                // 点击位置
                int selectedIndex = JBList.locationToIndex(e.getPoint());

                // 左键点击
                if (e.getButton() == MouseEvent.BUTTON1) {

                    // 点击 more 按钮
                    if (!plugin.notMavenSearchTab() && selectedIndex != -1 && listModel.isMoreElement(selectedIndex)) {
                        String pattern = context.getSearchText();
                        MavenSearchResult result = plugin.getDatabase().select(pattern);
                        if (result != null) { // 判断是否满足执行点击更多链接的条件
                            if (log.isDebugEnabled()) {
                                log.debug("Click '... more' button ..");
                            }

                            String message = MavenSearchMessage.get("maven.search.pattern.text", StringUtils.escapeLineSeparator(pattern));
                            plugin.setStatusbarText(MavenSearchAdvertiser.RUNNING, message);
                            plugin.execute(new MavenSearchMoreJob(pattern));
                        }
                        return;
                    }

                    // 左键点击导航项，隐藏右键菜单
                    Object selectedObject = listModel.getElementAt(selectedIndex);
                    if (selectedObject instanceof SearchNavigationItem) {
                        if (listPopupMenu.isVisible()) {
                            listPopupMenu.setVisible(false);
                        }
                    }
                    return;
                }

                // 右键点击
                if (e.getButton() == MouseEvent.BUTTON3) {

                    // 点击版本
                    Object selectedObject = listModel.getElementAt(selectedIndex);
                    if (selectedObject instanceof SearchNavigationItem) {
                        JBList.setSelectedIndex(selectedIndex);

                        SearchNavigationItem item = (SearchNavigationItem) selectedObject;
                        MavenArtifact artifact = item.getArtifact();

                        context.setSelectNavigationItem(item);
                        int x = JBList.getX() + 30;
                        int y = JBList.getCellBounds(0, selectedIndex).height; // JList 中第一行到选中行之间的高度

                        if (plugin.getLocalRepository().exists(artifact)) { // 工件在本地仓库中存在
                            listPopupMenu.add(openFileSystem);
                            listPopupMenu.add(deleteFile);
                            listPopupMenu.remove(downloadFile);
                            listPopupMenu.remove(cancelDownload);
                        } else {
                            listPopupMenu.remove(openFileSystem);
                            listPopupMenu.remove(deleteFile);

                            if (plugin.getService().isRunning(MavenSearchdDownloadJob.class, job -> job.getArtifact().equals(artifact))) {
                                listPopupMenu.remove(downloadFile);
                                listPopupMenu.add(cancelDownload);
                            } else {
                                listPopupMenu.add(downloadFile);
                                listPopupMenu.remove(cancelDownload);
                            }
                            listPopupMenu.remove(deleteFile);
                        }
                        listPopupMenu.show(JBList, x, y); // 在鼠标位置显示弹出菜单
                    }
                    return;
                }
            }
        });

        // 在搜索输入框下方，显示菜单
        JTextField searchField = context.getSearchField();
        searchField.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (plugin.notMavenSearchTab()) {
                    return;
                }

                if (e.getButton() == MouseEvent.BUTTON3) {
                    int x = searchField.getX();
                    int y = searchField.getY() - 30;
                    itemPopupMenu.show(JBList, x, y); // 在鼠标位置显示弹出菜单
                }
            }
        });
    }
}
