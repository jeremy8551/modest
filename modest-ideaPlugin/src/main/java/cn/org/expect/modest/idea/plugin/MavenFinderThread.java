package cn.org.expect.modest.idea.plugin;

import java.util.concurrent.LinkedBlockingQueue;

import cn.org.expect.util.Dates;
import com.intellij.openapi.diagnostic.Logger;

public class MavenFinderThread extends Thread {
    private static final Logger log = Logger.getInstance(MavenFinderThread.class);

    public final static MavenFinderThread INSTANCE = new MavenFinderThread();

    protected volatile LinkedBlockingQueue<String> queue;

    protected volatile boolean running;

    protected MavenFinderThread() {
        super();
        this.queue = new LinkedBlockingQueue<String>(10);
        this.running = true;
        this.setDaemon(true);
        this.start();
    }

    /**
     * 终止线程任务
     */
    public void terminate() {
        this.running = false;
        this.addPattern("");
    }

    /**
     * 添加搜索任务
     *
     * @param pattern 字符串
     */
    public void addPattern(String pattern) {
        try {
            this.queue.put(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        log.warn("start MavenFinder Search Thread ..");
        while (this.running) {
            try {
                String pattern = this.queue.take();
                Dates.sleep(500);
                if (this.queue.isEmpty()) {
                    MavenFinderStatement.INSTANCE.query(pattern);
                    
                    if (this.running) {
                        JListRenderer.INSTANCE.execute();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
