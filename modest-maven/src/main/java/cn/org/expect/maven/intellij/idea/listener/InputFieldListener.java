package cn.org.expect.maven.intellij.idea.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import cn.org.expect.maven.intellij.idea.MavenSearchPlugin;
import org.jetbrains.annotations.NotNull;

public class InputFieldListener extends KeyAdapter {

    private final MavenSearchPlugin plugin;

    public InputFieldListener(@NotNull MavenSearchPlugin plugin) {
        this.plugin = plugin;
    }

    public void keyPressed(KeyEvent e) {
        if (plugin.notMavenSearchTab()) {
            if (e.getKeyCode() == KeyEvent.VK_F2) {
                plugin.getContext().getSearchEverywhereUI().switchToTab(plugin.getContributor().getSearchProviderId());
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_F5) {
                plugin.repeat();
            }
        }
    }
}
