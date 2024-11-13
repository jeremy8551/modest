package cn.org.expect.maven.intellij.idea;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.intellij.idea.listener.InputFieldListener;
import cn.org.expect.maven.intellij.idea.listener.SearchListener;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationItem;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.maven.search.MavenSearchNotification;
import cn.org.expect.maven.search.MavenSearchUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.Advertiser;

public class MavenSearchPluginThread extends Thread {
    private final static Log log = LogFactory.getLog(MavenSearchPluginThread.class);

    private final MavenSearchPlugin plugin;

    public MavenSearchPluginThread(MavenSearchPlugin plugin) {
        super();
        this.setName(MavenSearchPluginThread.class.getSimpleName());
        this.plugin = Ensure.notNull(plugin);
    }

    @Override
    public void run() {
        log.info(MavenSearchMessage.get("maven.search.thread.start", this.getName()));

        MavenSearchPluginContext context = this.plugin.getContext(); // 上下文信息
        this.loadComponent(context); // 加载 UI 组件
        context.setLoadStatus(true); // 设置加载完成标志
        this.setPopupMenuUI(context); // 加载弹出菜单
        this.waitFor(context.getProgressIndicator(), 3000); // 等待 idea 默认的搜索功能执行完毕

        // 编辑器中选中的文本
        String editorSelectText = plugin.getContext().getEditorSelectText();
        if (StringUtils.isNotBlank(editorSelectText)) {
            String pattern = MavenSearchUtils.parse(editorSelectText);

            if (log.isDebugEnabled()) {
                log.debug("Idea editor selected text: {} --> {}", editorSelectText, pattern);
            }

            // 自动切换 Tab 页
            this.plugin.runEdtThread(() -> {
                plugin.setSearchFieldText(pattern); // 复制选中的文本到搜索栏

                if (MavenSearchUtils.isXML(editorSelectText)) {
                    context.getSearchEverywhereUI().switchToTab(context.getContributor().getSearchProviderId());
                }
            }, 0);
        }

        log.info(MavenSearchMessage.get("maven.search.thread.finish", this.getName()));
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
     * @param context 事件
     */
    protected void loadComponent(MavenSearchPluginContext context) {
        AnActionEvent event = context.getActionEvent();
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
        long startMillis = System.currentTimeMillis();
        while (!manager.isShown()) { // 等待对话框显示
            if (System.currentTimeMillis() - startMillis >= 3000) {
                break;
            } else {
                Dates.sleep(100);
            }
        }

        SearchEverywhereUI ui = manager.getCurrentlyShownUI();
        context.setSearchEverywhereUI(ui);
        ui.addSearchListener(new SearchListener(this.plugin));

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
            searchField.addKeyListener(new InputFieldListener(context)); // 打开搜索对话框后，点击 Shift 快捷键自动切换 Tab 页
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    protected void setPopupMenuUI(MavenSearchPluginContext context) {
        JPopupMenu listPopupMenu = new JPopupMenu();
        JMenuItem copyMaven = new JMenuItem(MavenSearchMessage.get("maven.search.btn.copy.maven.dependency.text")); // 复制 Maven 依赖
        JMenuItem copyGradle = new JMenuItem(MavenSearchMessage.get("maven.search.btn.copy.gradle.dependency.text")); // 复制 Gradle 依赖
        JMenuItem openInBrowser = new JMenuItem(MavenSearchMessage.get("maven.search.btn.open.in.browser.text")); // 在浏览器中打开
        JMenuItem openFileSystem = new JMenuItem(MavenSearchMessage.get("maven.search.btn.open.in.filesystem.text")); // 打开本地仓库目录
        JMenuItem downloadFile = new JMenuItem(MavenSearchMessage.get("maven.search.btn.download.local.repository.text")); // 下载按钮
        JMenuItem cancelDownload = new JMenuItem(MavenSearchMessage.get("maven.search.btn.cancel.download.local.repository.text")); // 取消下载按钮
        JMenuItem deleteFile = new JMenuItem(MavenSearchMessage.get("maven.search.btn.delete.local.repository.text")); // 删除本地仓库中的文件

        listPopupMenu.add(copyMaven); // 将菜单项添加到弹出菜单中
        listPopupMenu.add(copyGradle);
        listPopupMenu.add(openInBrowser);
        listPopupMenu.add(downloadFile);
        listPopupMenu.add(deleteFile);

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

            plugin.getServiceSearch().download(plugin, selectItem.getArtifact());
            plugin.repaintSearchResult();
        });

        // 取消下载
        cancelDownload.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            plugin.getServiceSearch().terminateDownloading();
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
                plugin.repaintSearchResult();
            }
        });

        // 重新执行查询
        repeat.addActionListener(e -> {
            String pattern = context.getSearchText();
            plugin.getDatabase().delete(pattern);
            plugin.asyncSearch(MavenSearchUtils.parse(pattern));
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
                if (plugin.notMavenSearchTab()) {
                    return;
                }

                // 左键点击
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int selectedIndex = JBList.getSelectedIndex();
                    if (selectedIndex != -1 && listModel.isMoreElement(selectedIndex)) { // 点击 more 按钮
                        String pattern = context.getSearchText();
                        MavenSearchResult result = plugin.getDatabase().select(pattern);
                        if (result != null && listModel.getFoundElementsInfo().size() >= result.size()) { // 判断是否满足执行点击更多链接的条件
                            if (log.isDebugEnabled()) {
                                log.debug("Click '... more' button ..");
                            }
                            plugin.getServiceSearch().searchMore(plugin, pattern);
                        }
                        return;
                    }

                    // 点击版本
                    Object selected = listModel.getElementAt(selectedIndex);
                    if (selected instanceof SearchNavigationItem) {
                        SearchNavigationItem item = (SearchNavigationItem) selected;
                        context.setSelectNavigationItem(item);
                        int x = JBList.getX() + 30;
                        int y = JBList.getCellBounds(0, selectedIndex).height; // JList 中第一行到选中行之间的高度

                        if (plugin.getLocalRepository().exists(item.getArtifact())) { // 工件在本地仓库中存在
                            listPopupMenu.add(openFileSystem);
                            listPopupMenu.add(deleteFile);
                            listPopupMenu.remove(downloadFile);
                            listPopupMenu.remove(cancelDownload);
                        } else {
                            listPopupMenu.remove(openFileSystem);
                            if (plugin.getServiceSearch().isDownloading(item.getArtifact())) {
                                listPopupMenu.remove(downloadFile);
                                listPopupMenu.add(cancelDownload);
                            } else {
                                listPopupMenu.add(downloadFile);
                                listPopupMenu.remove(cancelDownload);
                            }
                            listPopupMenu.remove(deleteFile);
                        }
                        listPopupMenu.show(JBList, x, y); // 在鼠标位置显示弹出菜单
                        return;
                    }
                }

                // 右键点击
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int index = JBList.locationToIndex(e.getPoint());
                    if (index == -1) {
                        return;
                    }

                    // 点击版本
                    Object selected = listModel.getElementAt(index);
                    if (selected instanceof SearchNavigationItem) {
                        if (listPopupMenu.isVisible()) {
                            listPopupMenu.setVisible(false);
                        }
                        return;
                    }
                }
            }
        });

        JTextField searchField = context.getSearchField();
        searchField.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (plugin.notMavenSearchTab()) {
                    return;
                }

                // 左键点击
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int x = searchField.getX();
                    int y = searchField.getY() - 30;
                    itemPopupMenu.show(JBList, x, y); // 在鼠标位置显示弹出菜单
                }
            }
        });
    }
}
