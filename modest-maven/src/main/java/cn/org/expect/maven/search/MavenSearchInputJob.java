package cn.org.expect.maven.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import cn.org.expect.maven.concurrent.MavenSearchJob;
import cn.org.expect.maven.concurrent.MavenSearchPatternJob;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

/**
 * 监听并执行：用户输入的模糊查询
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
     */
    public synchronized void search(MavenSearch search, String pattern) {
        search.getService().terminate(MavenSearchPatternJob.class, p -> true);

        try {
            MavenSearchPatternJob job = new MavenSearchPatternJob(pattern);
            job.setSearch(search);
            this.queue.put(job);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public int execute() throws Exception {
        log.info(MavenSearchMessage.get("maven.search.thread.start", this.getName()));
        while (!this.terminate) {
            try {
                MavenSearchPatternJob job = this.queue.take();
                String pattern = job.getPattern();
                MavenSearch search = job.getSearch();

                // 如果线程等待期间又添加了其他查询条件，则直接执行最后一个查询条件
                Dates.sleep(search.getContext().getInputIntervalTime());

                // 如果队列为空，表示在等待期间没有添加查询任务，则直接执行查询
                if (this.queue.isEmpty()) {
                    search.setProgressText(MavenSearchMessage.get("maven.search.progress.text"));
                    search.setStatusbarText(MavenSearchAdvertiser.RUNNING, MavenSearchMessage.get("maven.search.pattern.text", StringUtils.escapeLineSeparator(pattern)));
                    search.execute(job);
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        return 0;
    }
}
