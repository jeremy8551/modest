package cn.org.expect.maven.intellij.idea;

import cn.org.expect.maven.search.MavenMessage;
import cn.org.expect.maven.search.MavenUtils;
import cn.org.expect.maven.search.SearchOperation;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

public class MavenPluginThread extends Thread {
    private static final Logger log = Logger.getInstance(MavenPluginThread.class);

    private final MavenPlugin mavenFinder;

    public MavenPluginThread(SearchOperation mavenFinder) {
        super();
        this.setName(MavenPluginThread.class.getSimpleName());
        this.mavenFinder = Ensure.notNull(mavenFinder);
    }

    @Override
    public void run() {
        log.warn(MavenMessage.START_THREAD.fill(this.getName()));

        // 上下文信息
        MavenPluginContext context = this.mavenFinder.getContext();
        AnActionEvent event = context.getActionEvent();

        // 读取 Idea 搜索功能中的组件信息
        this.mavenFinder.detectIdeaComponent(event);

        // 添加弹出菜单
        new NavigationPopupMenu(this.mavenFinder);

        // 等待 idea 默认的搜索功能执行完毕
        this.mavenFinder.waitForSearchEverywhereUI(3000);

        // 复制编辑器中选中的内容到搜索栏
        String editorSelectText = context.getEditorSelectText();
        if (StringUtils.isNotBlank(editorSelectText)) {
            this.mavenFinder.setSearchFieldText(MavenUtils.parse(editorSelectText));
        }

        log.warn(MavenMessage.DETECTED_IDEA_UI_COMPONENT.fill(this.getName()));
    }
}
