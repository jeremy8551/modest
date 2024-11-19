package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import org.jetbrains.annotations.NotNull;

public class InputFieldListener extends KeyAdapter {

    private final MavenSearchPlugin plugin;

    public InputFieldListener(@NotNull MavenSearchPlugin plugin) {
        this.plugin = plugin;
    }

    public void keyPressed(KeyEvent e) {
        if (plugin.notMavenSearchTab()) {
            if (e.getKeyCode() == KeyEvent.VK_F2) { // F2 搜索
                plugin.getContext().getSearchEverywhereUI().switchToTab(plugin.getContributor().getSearchProviderId());
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_F5) { // F5 刷新
                plugin.repeat();
            }
        }
    }
}
