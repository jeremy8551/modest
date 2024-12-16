package cn.org.expect.intellij.idea.plugin.maven.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.maven.search.SearchNavigation;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.maven.search.ArtifactSearchNotification;

public abstract class MenuItemAction implements ActionListener {
    protected final static Log log = LogFactory.getLog(MenuItemAction.class);

    protected MavenSearchPlugin plugin;

    protected ActionEvent event;

    public MenuItemAction(MavenSearchPlugin plugin) {
        this.plugin = plugin;
    }

    public void actionPerformed(ActionEvent event) {
        this.event = event;
        MavenSearchPluginContext context = this.plugin.getContext();
        SearchNavigation navigation = context.geSelectedNavigation();
        if (navigation == null) {
            String message = MavenMessage.get("maven.search.error.not.select.search.result");
            if (log.isWarnEnabled()) {
                log.warn(message);
            }
            this.plugin.sendNotification(ArtifactSearchNotification.ERROR, message);
            return;
        }

        try {
            this.execute(navigation);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 执行业务逻辑
     *
     * @param navigation 选中的导航记录
     * @throws Exception 发生错误
     */
    public abstract void execute(SearchNavigation navigation) throws Exception;
}
