package cn.org.expect.maven.intellij.idea.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import cn.org.expect.maven.intellij.idea.MavenSearchPluginContext;

public class InputFieldListener extends KeyAdapter {

    private final MavenSearchPluginContext context;

    public InputFieldListener(MavenSearchPluginContext context) {
        this.context = context;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            context.getSearchEverywhereUI().switchToTab(context.getContributor().getSearchProviderId());
        }
    }
}
