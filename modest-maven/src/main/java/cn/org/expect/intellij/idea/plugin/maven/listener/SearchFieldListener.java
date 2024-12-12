package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import org.jetbrains.annotations.NotNull;

public class SearchFieldListener extends KeyAdapter {
    private final static Log log = LogFactory.getLog(SearchFieldListener.class);

    private final MavenSearchPlugin plugin;

    private volatile boolean isExt;

    public SearchFieldListener(@NotNull MavenSearchPlugin plugin) {
        this.plugin = plugin;
    }

    public void keyPressed(KeyEvent e) {
        this.isExt = e.isAltGraphDown() || e.isAltDown() || e.isActionKey() || e.isControlDown() || e.isMetaDown() || e.isShiftDown();

        if (plugin.isSelfTab()) {
            if (e.getKeyCode() == KeyEvent.VK_F5) { // F5 刷新
                plugin.asyncRefresh();
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_F2 && plugin.getSettings().isTabVisible()) { // F2 搜索
                String tabID = plugin.getContributor().getSearchProviderId();
                plugin.getIdeaUI().switchToTab(tabID);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (this.isExt) { // 不监听组合键
            return;
        }

//        if (this.plugin.canSearch()) {
//            char c = e.getKeyChar();
//            if (StringUtils.isLetter(c) || StringUtils.isNumber(c) || StringUtils.isSymbol(c)) { // 文本字符
//                String text = this.plugin.getPattern().filter(this.searchField.getText());
//
//                if (log.isDebugEnabled()) {
//                    log.debug("keyReleased tabID: {}, text: {}, letter: {}, keyCode: {}", this.plugin.getIdeaUI().getSelectedTabID(), text, c, e.getKeyCode());
//                }
//
//                MavenSearchPluginPinAction.PIN.extend(); // 扩展 pin 窗口大小
//                this.plugin.asyncSearch(text);
//            }
//        }
    }
}
