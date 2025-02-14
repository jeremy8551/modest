package cn.org.expect.concurrent.internal;

import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Terminate;
import cn.org.expect.util.TimeWatch;

/**
 * 并发任务的运行环境
 */
public class DefaultRunnable implements Runnable, Terminate {
    private final static Log log = LogFactory.getLog(DefaultRunnable.class);

    /** 回调接口 */
    private DefaultJobService service;

    /** 并发任务 */
    private final EasyJob job;

    /**
     * 初始化
     *
     * @param service 回调接口
     * @param easyJob 并发任务
     */
    public DefaultRunnable(DefaultJobService service, EasyJob easyJob) {
        super();
        this.service = Ensure.notNull(service);
        this.job = Ensure.notNull(easyJob);
    }

    public void run() {
        try {
            this.execute();
        } finally {
            if (this.service != null) {
                DefaultJobService service = this.service;
                this.service = null;
                service.executeNextJob();
            }
        }
    }

    /**
     * 执行并发任务
     */
    protected void execute() {
        TimeWatch watch = new TimeWatch();
        if (log.isDebugEnabled()) {
            log.debug("concurrent.stdout.message006", this.job.getName());
        }

        try {
            this.job.execute();
        } catch (Exception e) {
            String message = ResourcesUtils.getMessage("concurrent.stdout.message009", this.job.getName(), watch.useTime());
            this.service.writeError(this.job.getName(), message, e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("concurrent.stdout.message008", this.job.getName(), watch.useTime());
            }
        }
    }

    public void terminate() throws Exception {
        if (this.job != null && !this.job.isTerminate()) {
            this.job.terminate();
        }
    }

    public boolean isTerminate() {
        return this.job != null && this.job.isTerminate();
    }
}
