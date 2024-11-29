package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.IdeaSearchUI;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.intellij.idea.plugin.maven.SearchDisplay;
import cn.org.expect.intellij.idea.plugin.maven.listener.InputFieldListener;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationItem;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.maven.concurrent.MavenSearchDownloadJob;
import cn.org.expect.maven.concurrent.MavenSearchMoreJob;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenArtifactOperation;
import cn.org.expect.maven.repository.central.CentralRepository;
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
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.util.concurrency.EdtExecutorService;

public class MavenSearchPluginInitJob extends MavenSearchPluginJob {

    public int execute() {
        // 如果 manager.getCurrentlyShownUI() 报错，则捕获异常直接退出
        try {
            MavenSearchPlugin plugin = this.getSearch();
            this.setSearchEverywhereUI(plugin); // 加载 UI 组件
            this.setPopupMenuUI(plugin); // 加载弹出菜单
            this.setEditorSelectText(plugin);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 检测Idea的UI组件
     *
     * @param plugin 事件
     */
    protected void setSearchEverywhereUI(MavenSearchPlugin plugin) {
        MavenSearchPluginContext context = plugin.getContext();
        AnActionEvent event = context.getActionEvent();
        SearchEverywhereUI ui = this.getSearchEverywhereUI(event);
        plugin.getService().setParameter(MavenSearchExecutorServiceImpl.PARAMETER, JavaDialectFactory.get().getField(ui, "rebuildListAlarm"));
        plugin.getIdeaUI().setSearchEverywhereUI(ui);
        ui.addSearchListener(plugin.getSearchListener());
    }

    public SearchEverywhereUI getSearchEverywhereUI(AnActionEvent event) {
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
        long startMillis = System.currentTimeMillis();
        while (!manager.isShown()) { // 等待 SearchEverywhere 显示
            if (System.currentTimeMillis() - startMillis >= 10000) {
                break;
            } else {
                Dates.sleep(100);
            }
        }

        // 得到当前已显示的 SearchEverywhere 对象
        return manager.getCurrentlyShownUI(); // TODO 修改注册项后，再打开查询界面，这个位置报错 isShown
    }

    protected void setPopupMenuUI(MavenSearchPlugin plugin) {
        MavenSearchPluginContext context = plugin.getContext();
        JPopupMenu listPopupMenu = new JPopupMenu();
        JMenuItem copyMaven = new JMenuItem(MavenSearchMessage.get("maven.search.btn.copy.maven.dependency.text")); // 复制 Maven 依赖
        JMenuItem copyGradle = new JMenuItem(MavenSearchMessage.get("maven.search.btn.copy.gradle.dependency.text")); // 复制 Gradle 依赖
        JMenuItem openInCentralRepository = new JMenuItem(MavenSearchMessage.get("maven.search.btn.open.in.browser.text")); // 在浏览器中打开
        JMenuItem openFileSystem = new JMenuItem(MavenSearchMessage.get("maven.search.btn.open.in.filesystem.text")); // 打开本地仓库目录
        JMenuItem downloadFile = new JMenuItem(MavenSearchMessage.get("maven.search.btn.download.local.repository.text")); // 下载按钮
        JMenuItem cancelDownload = new JMenuItem(MavenSearchMessage.get("maven.search.btn.cancel.download.local.repository.text")); // 取消下载按钮
        JMenuItem deleteFile = new JMenuItem(MavenSearchMessage.get("maven.search.btn.delete.local.repository.text")); // 删除本地仓库中的文件

        // 必须要有以下菜单
        listPopupMenu.add(copyMaven);
        listPopupMenu.add(copyGradle);

        JPopupMenu itemPopupMenu = new JPopupMenu();
        JMenuItem repeat = new JMenuItem(MavenSearchMessage.get("maven.search.btn.refresh.query.text"));
        JMenuItem clearCache = new JMenuItem(MavenSearchMessage.get("maven.search.btn.clear.cache.text"));
        itemPopupMenu.add(repeat);
        itemPopupMenu.add(clearCache);

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

            String text = selectItem.getArtifact().toGroovyDSL();
            String filepath = plugin.getContext().getActionEvent().getProject().getBasePath();
            if (FileUtils.isDirectory(filepath)) {
                File dir = new File(filepath);
                if (!FileUtils.find(dir, "build.gradle.kts").isEmpty()) {
                    text = selectItem.getArtifact().toKotlinDSL();
                } else if (!FileUtils.find(dir, "build.gradle").isEmpty()) {
                    text = selectItem.getArtifact().toGroovyDSL();
                }
            }

            plugin.copyToClipboard(text);
            plugin.sendNotification(MavenSearchNotification.NORMAL, copyGradle.getText());
        });

        // 在 Maven 中央仓库浏览
        openInCentralRepository.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            MavenArtifact artifact = selectItem.getArtifact();
            List<String> list = new ArrayList<>();
            list.add(plugin.getEasyContext().getBean(CentralRepository.class).getAddress());
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
            plugin.setStatusBar(MavenSearchAdvertiser.RUNNING, message);
            plugin.execute(new MavenSearchDownloadJob(artifact));
            plugin.display();
        });

        // 取消下载
        cancelDownload.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectNavigationItem();
            if (selectItem == null) {
                log.warn("Not a selected Navigation Item!");
                return;
            }

            MavenArtifact artifact = selectItem.getArtifact();
            plugin.getService().terminate(MavenSearchDownloadJob.class, job -> job.getArtifact().equals(artifact));
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
                    log.debug("{} delete local repository {} ..", this.getName(), dir.getAbsolutePath());
                }
                FileUtils.delete(dir);
                plugin.display();
            }
        });

        // 重新执行查询
        repeat.addActionListener(e -> {
            plugin.asyncRefresh();
            plugin.sendNotification(MavenSearchNotification.NORMAL, repeat.getText());
        });

        // 清空所有缓存
        clearCache.addActionListener(e -> {
            plugin.getDatabase().clear();
            plugin.setProgress("");
            plugin.setStatusBar(null, "");
            plugin.getContext().setSearchText(null);
            plugin.getIdeaUI().getSearchField().setText("");
            plugin.getContext().setSearchResult(null);
            plugin.getContext().setNavigationResultSet(null);
            plugin.getContext().setSelectNavigationHead(null);
            plugin.getContext().setSelectNavigationItem(null);
            plugin.display(); // 刷新一个空结果
            plugin.sendNotification(MavenSearchNotification.NORMAL, clearCache.getText());
        });

        IdeaSearchUI ui = plugin.getIdeaUI();
        SearchDisplay display = ui.getDisplay();
        display.addMouseListener(new MouseAdapter() { // 监听鼠标事件

            public void mousePressed(MouseEvent e) {
                int selectedIndex = display.locationToIndex(e.getPoint()); // 点击位置

                // 左键点击 more 按钮
                if (e.getButton() == MouseEvent.BUTTON1 && plugin.canSearch() && selectedIndex != -1 && display.isMore(selectedIndex)) {
                    if (log.isDebugEnabled()) {
                        log.debug("{} Click more button {}", getName(), selectedIndex);
                    }

                    plugin.execute(new MavenSearchMoreJob());
                    return;
                }

                // 右键点击
                if (e.getButton() == MouseEvent.BUTTON3) {
                    Object selectedObject = display.getElement(selectedIndex); // 点击的导航记录

                    if (selectedObject instanceof SearchNavigationHead) {
                        context.setSelectNavigationHead((SearchNavigationHead) selectedObject);
                        return;
                    }

                    if (selectedObject instanceof SearchNavigationItem) {
                        context.setSelectNavigationItem((SearchNavigationItem) selectedObject);
                        display.setSelectedIndex(selectedIndex);

                        SearchNavigationItem item = (SearchNavigationItem) selectedObject;
                        MavenArtifact artifact = item.getArtifact();

                        int x = display.getX() + 30;
                        int y = display.getCellBounds(0, selectedIndex).height; // JList 中第一行到选中行之间的高度

                        MavenArtifactOperation operation = plugin.getRepository().getSupported();
                        if (plugin.getLocalRepository().exists(artifact)) { // 工件在本地仓库中存在
                            if (operation.supportOpenInCentralRepository()) {
                                listPopupMenu.add(openInCentralRepository);
                            }

                            if (operation.supportOpenInFileSystem()) {
                                listPopupMenu.add(openFileSystem);
                            }

                            if (operation.supportDelete()) {
                                listPopupMenu.add(deleteFile);
                            }

                            listPopupMenu.remove(downloadFile);
                            listPopupMenu.remove(cancelDownload);
                        } else {
                            if (operation.supportOpenInCentralRepository()) {
                                listPopupMenu.add(openInCentralRepository);
                            }

                            listPopupMenu.remove(openFileSystem);
                            listPopupMenu.remove(deleteFile);

                            if (plugin.getService().isRunning(MavenSearchDownloadJob.class, job -> job.getArtifact().equals(artifact))) {
                                listPopupMenu.remove(downloadFile);

                                if (operation.supportDownload()) {
                                    listPopupMenu.add(cancelDownload);
                                }
                            } else {
                                if (operation.supportDownload()) {
                                    listPopupMenu.add(downloadFile);
                                }
                                listPopupMenu.remove(cancelDownload);
                            }
                            listPopupMenu.remove(deleteFile);
                        }

                        // 在鼠标位置显示弹出菜单
                        display.showMenu(listPopupMenu, x, y);
                        return;
                    }
                }
            }
        });

        // 在搜索输入框下方，显示菜单
        try {
            JTextField searchField = ui.getSearchField();
            searchField.addKeyListener(new InputFieldListener(plugin, searchField));
            searchField.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    if (plugin.isSelfTab() && e.getButton() == MouseEvent.BUTTON3) { // 输入框右键，弹出菜单
                        Point location = searchField.getLocation();
                        Dimension size = searchField.getSize();
                        int x = location.x;
                        int y = Math.abs(location.y + size.height - 30);
                        itemPopupMenu.show(searchField, x, y); // 在鼠标位置显示弹出菜单
                    }
                }
            });
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 将编辑器中选中的文本，复制到 Tab 页的输入框中
     *
     * @param plugin 搜索接口
     */
    private void setEditorSelectText(MavenSearchPlugin plugin) {
        // 等待 Idea 默认的搜索功能执行完毕
        plugin.getIdeaUI().waitFor(2000);

        // 在已打开的编辑器中，如果选中了文本，则自动对文本进行查询
        Editor editor = plugin.getContext().getActionEvent().getDataContext().getData(CommonDataKeys.EDITOR);

        // 只能使用 EdtExecutorService.getInstance() 不能递归调用 plugin.execute() 方法
        if (editor != null) {
            EdtExecutorService.getInstance().execute(new MavenSearchEDTJob(() -> {
                String editorSelectText = StringUtils.trimBlank(editor.getSelectionModel().getSelectedText());
                if (StringUtils.isNotBlank(editorSelectText)) {

                    // 编辑器中选中的文本
                    String pattern = MavenSearchUtils.parse(editorSelectText);
                    if (log.isDebugEnabled()) {
                        log.debug("{} Idea editor selected text: {} --> {}", getName(), editorSelectText, pattern);
                    }

                    // 复制选中的文本到搜索栏
                    plugin.getIdeaUI().getSearchField().setText(pattern);

                    // 自动切换 Tab 页
                    if (plugin.getSettings().isAutoSwitchTab() && plugin.getSettings().isTabVisible() && MavenSearchUtils.isXML(editorSelectText)) {
                        plugin.getIdeaUI().switchToTab(plugin.getContributor().getSearchProviderId());
                        plugin.asyncSearch();
                    }
                } else {
                    // 如果未选中任何内容，则自动搜索输入框中的文本
                    if (plugin.canSearch()) {
                        plugin.asyncSearch();
                    }
                }
            }));
        }
    }
}
