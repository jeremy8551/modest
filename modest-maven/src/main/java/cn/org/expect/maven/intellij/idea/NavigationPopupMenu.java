package cn.org.expect.maven.intellij.idea;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationItem;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.search.MavenSearchNotification;
import cn.org.expect.maven.search.MavenSearchUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.ui.components.JBList;

public class NavigationPopupMenu {
    private final static Log log = LogFactory.getLog(NavigationPopupMenu.class);

    private final MavenSearchPlugin plugin;

    public NavigationPopupMenu(MavenSearchPlugin plugin) {
        this.plugin = Ensure.notNull(plugin);
        this.init();
    }

    protected void init() {
        JPopupMenu listPopupMenu = new JPopupMenu();
        JMenuItem copyMaven = new JMenuItem("Copy Maven dependency");
        JMenuItem copyGradle = new JMenuItem("Copy Gradle dependency");
        JMenuItem openInBrowser = new JMenuItem("Open in Browser");
        JMenuItem openFileSystem = new JMenuItem("Open in FileSystem");
        listPopupMenu.add(copyMaven); // 将菜单项添加到弹出菜单中
        listPopupMenu.add(copyGradle);
        listPopupMenu.add(openInBrowser);

        JPopupMenu itemPopupMenu = new JPopupMenu();
        JMenuItem repeat = new JMenuItem("Refresh the query");
        JMenuItem clearCache = new JMenuItem("Clear all cache");
        itemPopupMenu.add(repeat);
        itemPopupMenu.add(clearCache);

        MavenPluginContext context = this.plugin.getContext();
        JBList<Object> JBList = context.getJBList();
        SearchListModel listModel = context.getJBListModel();

        // 添加菜单项的操作
        copyMaven.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectItem();
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

        copyGradle.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectItem();
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

        openInBrowser.addActionListener(e -> {
            SearchNavigationItem selectItem = context.getSelectItem();
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

        openFileSystem.addActionListener(e -> {
            String filepath = plugin.getLocalRepository().getAddress();
            if (StringUtils.isBlank(filepath)) {
                return;
            }

            SearchNavigationItem selectItem = context.getSelectItem();
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
            plugin.repaint(new SimpleMavenSearchResult()); // 刷新一个空结果
            plugin.setWaitingText("");
            plugin.setRunningText(null, "");
            plugin.setSearchText("");
            plugin.getContext().setNavigationResultSet(null);
            plugin.sendNotification(MavenSearchNotification.NORMAL, clearCache.getText());
        });

        // 监听鼠标事件
        JBList.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // 左键点击
                if (e.getButton() == MouseEvent.BUTTON1) {

                    // 根据点击位置，获得对应的导航记录
                    int index = JBList.locationToIndex(e.getPoint());
                    if (index == -1) {
                        return;
                    }

                    // 点击版本
                    Object selected = listModel.getElementAt(index);
                    if (selected instanceof SearchNavigationItem) {
                        SearchNavigationItem item = (SearchNavigationItem) selected;
                        context.setSelectItem(item);
                        int x = JBList.getX() + 30;
                        int y = JBList.getCellBounds(0, index).height; // JList 中第一行到选中行之间的高度

                        if (plugin.getLocalRepository().exists(item.getArtifact())) {
                            listPopupMenu.add(openFileSystem);
                        } else {
                            listPopupMenu.remove(openFileSystem);
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

            @Override
            public void mousePressed(MouseEvent e) {
                if (plugin.notMavenSearchTab()) {
                    return;
                }

                int selectedIndex = JBList.getSelectedIndex();
                if (selectedIndex != -1 && listModel.isMoreElement(selectedIndex)) { // 点击 more 按钮
                    String pattern = context.getSearchText();
                    MavenSearchResult result = plugin.getDatabase().select(pattern);
                    if (result != null && listModel.getFoundElementsInfo().size() >= result.size()) { // 判断是否满足插叙更多记录的条件
                        log.info("Click More Button ..");
                        plugin.getServiceSearch().searchMore(plugin, pattern);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        JTextField searchField = context.getSearchField();
        if (searchField != null) {
            searchField.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // 右键点击
                    if (e.isPopupTrigger()) {
                        int x = searchField.getX();
                        int y = searchField.getY() - 30;
                        itemPopupMenu.show(JBList, x, y); // 在鼠标位置显示弹出菜单
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
        }
    }
}
