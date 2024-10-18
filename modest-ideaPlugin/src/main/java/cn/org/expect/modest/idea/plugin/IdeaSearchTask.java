package cn.org.expect.modest.idea.plugin;

import java.util.function.Consumer;

import cn.org.expect.util.Dates;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class IdeaSearchTask implements Runnable {

    private static volatile SearchEverywhereUI UI;

    public static void repaint() {
        if (UI != null) {
            UI.repaint();
        }
    }

    private final AnActionEvent event;

    private final Consumer<SearchEverywhereUI> consumer;

    public IdeaSearchTask(AnActionEvent event, Consumer<SearchEverywhereUI> consumer) {
        this.event = event;
        this.consumer = consumer;
    }

    public void run() {
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(this.event.getProject());
        while (!manager.isShown()) { // 等待对话框显示
            Dates.sleep(100);
        }
        UI = manager.getCurrentlyShownUI();
        consumer.accept(UI);
    }

    public SearchEverywhereUI getUI() {
        if (UI == null) {
            synchronized (IdeaSearchTask.class) {
                if (UI == null) {

                }
            }
        }
        return UI;
    }
}
