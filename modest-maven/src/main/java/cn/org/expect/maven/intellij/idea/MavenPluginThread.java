package cn.org.expect.maven.intellij.idea;

import cn.org.expect.maven.search.MavenMessage;
import cn.org.expect.maven.search.MavenSearchUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

public class MavenPluginThread extends Thread {
    private static final Logger log = Logger.getInstance(MavenPluginThread.class);

    private final MavenSearchPlugin plugin;

    public MavenPluginThread(MavenSearchPlugin plugin) {
        super();
        this.setName(MavenPluginThread.class.getSimpleName());
        this.plugin = Ensure.notNull(plugin);
    }

    @Override
    public void run() {
        log.warn(MavenMessage.START_THREAD.fill(this.getName()));

        // 上下文信息
        MavenPluginContext context = this.plugin.getContext();
        AnActionEvent event = context.getActionEvent();

        // 读取 Idea 搜索功能中的组件信息
        this.plugin.detectIdeaComponent(event);

        // 添加弹出菜单
        new NavigationPopupMenu(this.plugin);

        // 等待 idea 默认的搜索功能执行完毕
        this.plugin.waitForSearchEverywhereUI(3000);

        // 复制编辑器中选中的内容到搜索栏
        String editorSelectText = context.getEditorSelectText();
        if (StringUtils.isNotBlank(editorSelectText)) {
            this.plugin.setSearchText(MavenSearchUtils.parse(editorSelectText));
        }

        log.warn(MavenMessage.DETECTED_IDEA_UI_COMPONENT.fill(this.getName()));
    }
}
