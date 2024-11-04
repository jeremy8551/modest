package cn.org.expect.intellijidea.plugin.maven;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

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
        JMenuItem copyMaven = new JMenuItem("复制 Maven 依赖");
        JMenuItem copyGradle = new JMenuItem("复制 Gradle 依赖");
        JMenuItem clearCache = new JMenuItem("重新加载数据");
        JMenuItem clearAll = new JMenuItem("清空全部缓存");

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(copyMaven); // 将菜单项添加到弹出菜单中
        popupMenu.add(copyGradle);
        popupMenu.add(clearCache);
        popupMenu.add(clearAll);

        MavenFinderContext context = this.mavenFinder.getContext();
        JBList<Object> JBList = context.getJBList();
        SearchListModel searchListModel = context.getJBListModel();

        // 添加菜单项的操作
        copyMaven.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        copyGradle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        clearCache.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pattern = context.getSearchPattern();
                mavenFinder.getDatabase().delete(pattern);
                mavenFinder.asyncSearch(MavenFinderPattern.parse(pattern));
                mavenFinder.sendMessage(MavenFinder.class.getSimpleName(), "正在重新加载数据", null);
            }
        });

        clearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mavenFinder.getDatabase().clear();
                mavenFinder.sendMessage(MavenFinder.class.getSimpleName(), "已清空所有缓存", null);
            }
        });

        // 监听鼠标事件
        JBList.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // 左键点击
                    int selectedIndex = JBList.getSelectedIndex();
                    int size = searchListModel.getSize();

                    // 点击 more 按钮
                    if (selectedIndex == -1) {
                        mavenFinder.getSearchPattern().search(mavenFinder, context.getSearchPattern());
                        return;
                    }

                    Object selectedObject = searchListModel.getElementAt(selectedIndex);
                    if (selectedObject instanceof MavenFinderNavigationItem) {
                        context.setSelectItem((MavenFinderNavigationItem) selectedObject);
                        int x = JBList.getX() + 30;
                        int y = JBList.getCellBounds(0, selectedIndex).height; // JList 中第一行到选中行之间的高度
                        popupMenu.show(JBList, x, y); // 在鼠标位置显示弹出菜单
                        return;
                    }
                }

                if (e.getButton() == MouseEvent.BUTTON3) { // 右键点击
                    if (popupMenu.isVisible()) {
                        popupMenu.setVisible(false);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
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
