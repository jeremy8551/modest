package cn.org.expect.modest.idea.plugin.db;

import java.util.concurrent.LinkedBlockingQueue;

import cn.org.expect.modest.idea.plugin.MavenFinderIcons;
import cn.org.expect.modest.idea.plugin.ui.IntelliJIdea;
import cn.org.expect.modest.idea.plugin.ui.JListRenderer;
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
            String message = "<html><span style='color:orange;'>Search " + groupId + ":" + artifactId + " in Maven Repository ..</span></html>";
            IntelliJIdea.updateAdvertiser(message, MavenFinderIcons.MAVEN_REPOSITORY_BOTTOM_WAITING);

            try {
                this.queue.put(new String[]{groupId, artifactId});
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public void run() {
        log.warn("start MavenFinder Search Extra Thread ..");
        while (this.running) {
            try {
                String[] array = this.queue.take();
                String groupId = array[0];
                String artifactId = array[1];

                if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
                    MavenFinderResult result = MavenSearchStatement.INSTANCE.query(groupId, artifactId);
                    if (result != null && this.queue.isEmpty()) { // 如果没有其他任务，则重新渲染UI
                        JListRenderer.INSTANCE.execute(MavenSearchStatement.INSTANCE.last());
                    }
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * 判断当前是否正在查询某个 Maven 工件
     *
     * @param groupId    域名
     * @param artifactId 工件名
     * @return 返回true表示正在查询
     */
    public boolean isExtraQuerying(String groupId, String artifactId) {
        for (String[] array : this.queue) {
            String g = array[0];
            String a = array[1];
            if (g.equals(groupId) && a.equals(artifactId)) {
                return true;
            }
        }
        return false;
    }
}
