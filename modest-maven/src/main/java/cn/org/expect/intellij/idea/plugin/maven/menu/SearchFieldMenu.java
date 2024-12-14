package cn.org.expect.intellij.idea.plugin.maven.menu;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigation;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.maven.search.ArtifactSearchNotification;

/**
 * 搜索输入框中弹出的菜单
 */
public class SearchFieldMenu extends AbstractMenu {

    private final JPopupMenu topMenu = new JPopupMenu();
    private final JMenuItem repeat = new JMenuItem(MavenMessage.get("maven.search.btn.refresh.query.text"));
    private final JMenuItem clearCache = new JMenuItem(MavenMessage.get("maven.search.btn.clear.cache.text"));

    public SearchFieldMenu(MavenSearchPlugin plugin) {
        super(plugin);
        this.topMenu.add(repeat);
        this.topMenu.add(clearCache);
        this.addAction(plugin);
    }

    protected void addAction(MavenSearchPlugin plugin) {
        // 重新执行查询
        repeat.addActionListener(new MenuItemAction(plugin) {
            public void execute(MavenSearchNavigation navigation) {
                plugin.asyncRefresh();
                plugin.sendNotification(ArtifactSearchNotification.NORMAL, repeat.getText());
            }
        });

        // 清空所有缓存
        clearCache.addActionListener(new MenuItemAction(plugin) {
            public void execute(MavenSearchNavigation navigation) {
                // 清空所有数据库缓存
                List<EasyBeanInfo> list = plugin.getIoc().getBeanInfoList(ArtifactRepositoryDatabaseEngine.class);
                for (EasyBeanInfo beanInfo : list) {
                    ArtifactRepositoryDatabaseEngine engine = plugin.getIoc().getBean(beanInfo.getType());
                    if (engine != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("{} clear()", engine.getClass().getName());
                        }
                        engine.clear();
                    }
                }

                plugin.setProgress("");
                plugin.setStatusBar(null, "");
                plugin.getContext().setSearchText(null);
                plugin.getIdeaUI().getSearchField().setText("");
                plugin.getContext().setSearchResult(null);
                plugin.getContext().setNavigationList(null);
                plugin.getContext().setSelectNavigation(null);
                plugin.display(); // 刷新一个空结果
                plugin.sendNotification(ArtifactSearchNotification.NORMAL, clearCache.getText());
            }
        });
    }

    public void mousePressed(MouseEvent e) {
        MavenSearchPlugin plugin = this.getPlugin();
        JTextField searchField = plugin.getIdeaUI().getSearchField();
        if (plugin.isSelfTab() && e.getButton() == MouseEvent.BUTTON3) { // 输入框右键，弹出菜单
            Point location = searchField.getLocation();
            Dimension size = searchField.getSize();
            int x = location.x;
            int y = Math.abs(location.y + size.height - 30);
            topMenu.show(searchField, x, y); // 在鼠标位置显示弹出菜单
        }
    }
}
