package cn.org.expect.maven.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

/**
 * 守护线程任务，监听并执行用户输入的模糊查询
 */
public class MavenSearchInputJob extends MavenSearchJob {

    /** 远程调用组件 */
    protected final BlockingQueue<MavenSearchPatternJob> queue;

    public MavenSearchInputJob() {
        super();
        this.queue = new LinkedTransferQueue<>();
    }

    /**
     * 执行模糊搜索
     *
     * @param search  搜索接口
     * @param pattern 字符串
     * @param delete  true表示先删除数据库中的记录再执行搜索，false表示直接执行搜索
     */
    public synchronized void search(MavenSearch search, String pattern, boolean delete) {
        search.getService().terminate(MavenSearchPatternJob.class, job -> true); // 终止正在运行的任务

        try {
            MavenSearchPatternJob job = new MavenSearchPatternJob(pattern, delete);
            job.setSearch(search);
            this.queue.put(job);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public int execute() throws Exception {
        while (!this.terminate) {
            try {
                MavenSearchPatternJob job = this.queue.take();
                String pattern = job.getPattern();
                MavenSearch search = job.getSearch();

                // 如果线程等待期间又添加了其他查询条件，则直接执行最后一个查询条件
                Dates.sleep(search.getSettings().getInputIntervalTime());

                // 如果队列为空，表示在等待期间没有添加查询任务，则直接执行查询
                if (this.queue.isEmpty()) {
                    search.setProgress(MavenSearchMessage.get("maven.search.progress.text"));
                    search.setStatusBar(MavenSearchAdvertiser.RUNNING, MavenSearchMessage.get("maven.search.pattern.text", StringUtils.escapeLineSeparator(pattern)));
                    search.execute(job);
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return 0;
    }
}
