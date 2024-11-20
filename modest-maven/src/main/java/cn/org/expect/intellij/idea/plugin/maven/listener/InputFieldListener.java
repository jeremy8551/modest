package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import org.jetbrains.annotations.NotNull;

public class InputFieldListener extends KeyAdapter {
    private final static Log log = LogFactory.getLog(InputFieldListener.class);

    private final JTextField searchField;

    private final MavenSearchPlugin plugin;

    public InputFieldListener(@NotNull MavenSearchPlugin plugin, JTextField searchField) {
        this.plugin = plugin;
        this.searchField = searchField;
    }

    public void keyPressed(KeyEvent e) {
        if (plugin.isSelfTab()) {
            if (e.getKeyCode() == KeyEvent.VK_F5) { // F5 刷新
                plugin.asyncRefresh();
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_F2) { // F2 搜索
                String tabID = plugin.getContributor().getSearchProviderId();
                plugin.getIdeaUI().switchToTab(tabID);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (this.plugin.canSearch()) {
            if (log.isDebugEnabled()) {
                log.debug("keyReleased tabID: {}, text: {}", this.plugin.getIdeaUI().getSelectedTabID(), this.searchField.getText());
            }
            this.plugin.asyncSearch(this.searchField.getText());
        }
    }
}
