package cn.org.expect.intellij.idea.plugin.maven.menu;

import java.awt.event.MouseAdapter;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;

public class AbstractMenu extends MouseAdapter {
    protected final static Log log = LogFactory.getLog(AbstractMenu.class);

    /** 搜索接口 */
    private final MavenSearchPlugin plugin;

    public AbstractMenu(MavenSearchPlugin plugin) {
        this.plugin = Ensure.notNull(plugin);
    }

    /**
     * 返回搜索接口
     *
     * @return 搜索接口
     */
    public MavenSearchPlugin getPlugin() {
        return this.plugin;
    }
}
