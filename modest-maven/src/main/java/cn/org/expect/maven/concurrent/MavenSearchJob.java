package cn.org.expect.maven.concurrent;

import cn.org.expect.concurrent.BaseJob;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.search.MavenSearchAware;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

public abstract class MavenSearchJob extends BaseJob implements Runnable, MavenSearchAware {
    protected final static Log log = LogFactory.getLog(MavenSearchJob.class);

    /** 线程池 */
    protected volatile MavenSearchExecutorService service;

    /** 搜索接口 */
    private MavenSearch search;

    /** Maven仓库接口 */
    private volatile MavenRepository remoteRepository;

    /** true表示任务执行完毕，false表示未执行完毕 */
    private volatile boolean finish;

    /** true表示任务正在运行 false表示任务没有运行 */
    private volatile boolean running;

    public MavenSearchJob() {
        this.terminate = false;
        this.setName(this.getClass().getSimpleName());
        this.finish = false;
        this.running = false;
    }

    public void setService(MavenSearchExecutorService service) {
        this.service = Ensure.notNull(service);
    }

    public void setSearch(MavenSearch search) {
        this.search = search;
    }

    public MavenSearch getSearch() {
        return search;
    }

    protected MavenRepository getRemoteRepository() {
        if (this.remoteRepository == null) {
            synchronized (this) {
                if (this.remoteRepository == null) {
                    this.remoteRepository = search.getRepository();
                }
            }
        }
        return this.remoteRepository;
    }

    public void terminate() {
        if (log.isDebugEnabled()) {
            log.debug("{} terminated!", this.getName());
        }

        super.terminate();
        if (this.remoteRepository != null) {
            this.remoteRepository.terminate();
        }
    }

    public final void run() {
        String split = StringUtils.left("", 100, '-');
        if (log.isInfoEnabled()) {
            log.info(split + "\n" + MavenSearchMessage.get("maven.search.thread.start", this.getName()));
        }

        this.running = true;
        this.finish = false;
        try {
            if (!this.terminate) {
                this.execute();
            }
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            this.finish = true;
            this.running = false;

            // 任务执行完毕，从线程池移除
            if (this.service != null) {
                try {
                    this.service.removeJob(this);
                } catch (Throwable e) {
                    log.error(e.getLocalizedMessage(), e);
                }
            }

            if (log.isInfoEnabled()) {
                log.info(MavenSearchMessage.get("maven.search.thread.finish", this.getName()) + "\n" + split + "\n\n");
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean isFinish() {
        return this.finish;
    }
}
