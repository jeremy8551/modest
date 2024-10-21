package cn.org.expect.modest.idea.plugin;

import java.util.concurrent.LinkedBlockingQueue;

import cn.org.expect.util.StringUtils;
import com.intellij.openapi.diagnostic.Logger;

/**
 * （按groupId 与 artifactId）精确查询使用的线程
 */
public class MavenSearchExtraThread extends Thread {
    private static final Logger log = Logger.getInstance(MavenSearchExtraThread.class);

    public final static MavenSearchExtraThread INSTANCE = new MavenSearchExtraThread();

    protected volatile LinkedBlockingQueue<String[]> queue;

    protected volatile boolean running;

    protected MavenSearchExtraThread() {
        super();
        this.queue = new LinkedBlockingQueue<String[]>(10);
        this.running = true;
        this.setDaemon(true);
        this.setName(MavenSearchExtraThread.class.getSimpleName());
    }

    /**
     * 终止线程任务
     */
    public void terminate() {
        this.running = false;
    }

    public void search(String groupId, String artifactId) {
        if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
            try {
                this.queue.put(new String[]{groupId, artifactId});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        log.warn("start MavenFinder Search Extra Thread ..");
        while (this.running) {
            try {
                String[] array = this.queue.take();
                MavenFinderResult result = MavenSearchStatement.INSTANCE.query(array[0], array[1]);
                if (result != null && this.queue.isEmpty()) { // 如果没有其他任务，则重新渲染UI
                    JListRenderer.INSTANCE.execute(MavenSearchStatement.INSTANCE.last());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
