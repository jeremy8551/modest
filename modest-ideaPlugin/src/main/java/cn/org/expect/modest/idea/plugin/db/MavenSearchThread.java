package cn.org.expect.modest.idea.plugin.db;

import java.util.concurrent.LinkedBlockingQueue;

import cn.org.expect.modest.idea.plugin.MavenFinderIcons;
import cn.org.expect.modest.idea.plugin.ui.IntelliJIdea;
import cn.org.expect.modest.idea.plugin.ui.JListRenderer;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.diagnostic.Logger;

public class MavenSearchThread extends Thread {
    private static final Logger log = Logger.getInstance(MavenSearchThread.class);

    public final static MavenSearchThread INSTANCE = new MavenSearchThread();

    protected volatile LinkedBlockingQueue<String> queue;

    protected volatile boolean running;

    protected MavenSearchThread() {
        super();
        this.queue = new LinkedBlockingQueue<String>(10);
        this.running = true;
        this.setDaemon(true);
        this.setName(MavenSearchThread.class.getSimpleName());
    }

    /**
     * 终止线程任务
     */
    public void terminate() {
        this.running = false;
        this.add(""); // 唤醒当前线程
    }

    /**
     * 添加搜索任务
     *
     * @param pattern 字符串
     */
    public void search(String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            IntelliJIdea.updateAdvertiser("<html><span style='color:orange;'>Search " + pattern + " in Maven Repository ..</span></html>", MavenFinderIcons.MAVEN_REPOSITORY_BOTTOM_WAITING);
            IntelliJIdea.JLIST_SELECT_ITEM = null;
            this.add(pattern);
        }
    }

    private void add(String pattern) {
        try {
            this.queue.put(pattern);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void run() {
        log.warn("start MavenFinder Search Thread ..");
        while (this.running) {
            try {
                String pattern = this.queue.take();
                IntelliJIdea.updateJListText("Search in Maven Repository ..");

                // 如果线程等待期间又添加了其他查询条件，则直接执行最后一个查询条件
                Dates.sleep(400);

                // 查询
                if (StringUtils.isNotBlank(pattern) && this.queue.isEmpty()) {
                    MavenFinderResult result = MavenSearchStatement.INSTANCE.query(pattern);
                    if (result == null) {
                        String message = "Failed to send query request to Maven Repository!";
                        IntelliJIdea.updateJListText(message);
                        IntelliJIdea.updateAdvertiser(message, MavenFinderIcons.MAVEN_REPOSITORY_BOTTOM);
                    } else if (result.size() == 0) {
                        String message = "Nothing found.";
                        IntelliJIdea.updateJListText(message);
                        IntelliJIdea.updateAdvertiser(message, MavenFinderIcons.MAVEN_REPOSITORY_BOTTOM);
                    } else {
                        IntelliJIdea.clearJListText();
                        JListRenderer.INSTANCE.execute(result);
                    }
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
