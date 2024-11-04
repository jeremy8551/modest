package cn.org.expect.intellijidea.plugin.maven;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

public class MavenFinderThread extends Thread {
    private static final Logger log = Logger.getInstance(MavenFinderThread.class);

    private final MavenFinder mavenFinder;

    public MavenFinderThread(MavenFinder mavenFinder) {
        super();
        this.setName(MavenFinderThread.class.getSimpleName());
        this.mavenFinder = Ensure.notNull(mavenFinder);
    }

    @Override
    public void run() {
        log.warn(MavenFinderMessage.START_THREAD.fill(this.getName()));

        // 上下文信息
        MavenFinderContext context = this.mavenFinder.getContext();
        AnActionEvent event = context.getActionEvent();

        // 读取 Idea 搜索功能中的组件信息
        this.mavenFinder.detectIdeaComponent(event);
        log.warn(MavenFinderMessage.DETECTED_IDEA_UI_COMPONENT.fill(this.getName()));
        new MavenFinderPopupMenu(this.mavenFinder);

        // 等待 idea 默认的搜索功能执行完毕
        this.mavenFinder.waitForSearchEverywhereUI(3000);

        // 复制编辑器中选中的内容到搜索栏
        String editorSelectText = context.getEditorSelectText();
        if (StringUtils.isNotBlank(editorSelectText)) {
            this.mavenFinder.setSearchFieldText(MavenFinderPattern.parse(editorSelectText));
//                try {
//                    if (MavenFinderPattern.isXML(editorSelectText)) {
//                        ideaSearchUI.switchToTab(contributor.getSearchProviderId()); // 选择标签页
//                        System.out.println("select Tab: " + ideaSearchUI.getSelectedTabID());
//                    }
//                } catch (Exception e) {
//                    log.error(e.getLocalizedMessage(), e);
//                }
        }
    }
}
