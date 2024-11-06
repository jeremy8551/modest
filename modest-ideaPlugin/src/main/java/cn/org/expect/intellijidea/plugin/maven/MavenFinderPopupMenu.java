package cn.org.expect.intellijidea.plugin.maven;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.impl.SimpleMavenSearchResult;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderNavigationItem;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.components.JBList;

public class MavenFinderPopupMenu {
    private static final Logger log = Logger.getInstance(MavenFinderPopupMenu.class);

    private MavenFinder mavenFinder;

    public MavenFinderPopupMenu(MavenFinder mavenFinder) {
        this.mavenFinder = Ensure.notNull(mavenFinder);
        this.init();
    }

    protected void init() {
        JPopupMenu listPopupMenu = new JPopupMenu();
        JMenuItem copyMaven = new JMenuItem("复制 Maven 依赖");
        JMenuItem copyGradle = new JMenuItem("复制 Gradle 依赖");
        listPopupMenu.add(copyMaven); // 将菜单项添加到弹出菜单中
        listPopupMenu.add(copyGradle);

        JPopupMenu itemPopupMenu = new JPopupMenu();
        JMenuItem clearCache = new JMenuItem("重新查询");
        JMenuItem clearAll = new JMenuItem("清空全部缓存");
        itemPopupMenu.add(clearCache);
        itemPopupMenu.add(clearAll);

        MavenFinderContext context = this.mavenFinder.getContext();
        JBList<Object> JBList = context.getJBList();
        SearchListModel listModel = context.getJBListModel();

        // 添加菜单项的操作
        copyMaven.addActionListener(e -> {
            MavenFinderNavigationItem selectItem = context.getSelectItem();
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

            mavenFinder.copyToClipboard(text);
            mavenFinder.sendMessage(MavenFinder.class.getSimpleName(), "已复制 Maven 依赖", null);
        });

        copyGradle.addActionListener(e -> {
            MavenFinderNavigationItem selectItem = context.getSelectItem();
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

            mavenFinder.copyToClipboard(text);
            mavenFinder.sendMessage(MavenFinder.class.getSimpleName(), "已复制 Gradle 依赖", null);
        });

        clearCache.addActionListener(e -> {
            String pattern = context.getSearchPattern();
            mavenFinder.getDatabase().delete(pattern);
            mavenFinder.asyncSearch(MavenFinderPattern.parse(pattern));
            mavenFinder.sendMessage(MavenFinder.class.getSimpleName(), "正在重新加载数据", null);
        });

        clearAll.addActionListener(e -> {
            mavenFinder.getDatabase().clear();
            mavenFinder.repaint(new SimpleMavenSearchResult()); // 刷新一个空结果
            mavenFinder.setReminderText("");
            mavenFinder.setAdvertiser("", null);
            mavenFinder.setSearchFieldText("");
            mavenFinder.sendMessage(MavenFinder.class.getSimpleName(), "已清空所有缓存", null);
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
                    Object selectedObject = listModel.getElementAt(index);
                    if (selectedObject instanceof MavenFinderNavigationItem) {
                        context.setSelectItem((MavenFinderNavigationItem) selectedObject);
                        int x = JBList.getX() + 30;
                        int y = JBList.getCellBounds(0, index).height; // JList 中第一行到选中行之间的高度
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
                    Object selectedObject = listModel.getElementAt(index);
                    if (selectedObject instanceof MavenFinderNavigationItem) {
                        if (listPopupMenu.isVisible()) {
                            listPopupMenu.setVisible(false);
                        }
                        return;
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int selectedIndex = JBList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Object selectedObject = listModel.getElementAt(selectedIndex);
                    if (selectedObject != null && selectedObject.getClass().equals(Object.class)) { // 点击 more 按钮
                        log.warn("Click More Button: " + selectedObject.getClass().getName());
                        mavenFinder.getSearch().searchMore(mavenFinder, context.getSearchPattern());
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
                        itemPopupMenu.show(JBList, e.getX(), e.getY()); // 在鼠标位置显示弹出菜单
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
