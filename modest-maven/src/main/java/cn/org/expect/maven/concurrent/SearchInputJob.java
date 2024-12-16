package cn.org.expect.maven.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import cn.org.expect.maven.search.ArtifactSearch;
import cn.org.expect.maven.search.ArtifactSearchStatusMessageType;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

/**
 * 守护线程任务，监听并执行用户输入的模糊查询
 */
public class SearchInputJob extends MavenJob {

    /** 远程调用组件 */
    protected final BlockingQueue<SearchPatternJob> queue;

    public SearchInputJob() {
        super("maven.search.job.search.pattern.daemon.description");
        this.queue = new LinkedTransferQueue<>();
    }

    /**
     * 执行模糊搜索
     *
     * @param search  搜索接口
     * @param pattern 字符串
     */
    public synchronized void search(ArtifactSearch search, String pattern) {
        search.getService().terminate(SearchPatternJob.class, job -> job.getClass().equals(SearchPatternJob.class)); // 终止正在运行的任务

        try {
            SearchPatternJob job = new SearchPatternJob(pattern);
            job.setSearch(search);
            this.queue.put(job);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public int execute() throws Exception {
        while (!this.terminate) {
            try {
                SearchPatternJob job = this.queue.take();
                String pattern = job.getPattern();
                ArtifactSearch search = job.getSearch();

                // 如果线程等待期间又添加了其他查询条件，则直接执行最后一个查询条件
                Dates.sleep(search.getSettings().getInputIntervalTime());

                // 如果队列为空，表示在等待期间没有添加查询任务，则直接执行查询
                if (this.queue.isEmpty()) {
                    search.setProgress("maven.search.progress.text", search.getRepositoryInfo().getDisplayName());
                    search.setStatusBar(ArtifactSearchStatusMessageType.RUNNING, "maven.search.pattern.text", StringUtils.escapeLineSeparator(pattern), search.getRepositoryInfo().getDisplayName());
                    search.execute(job);
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return 0;
    }
}
