package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;

public class MavenSearchEDTJob extends MavenSearchPluginJob implements EDTJob {
    protected final static Log log = LogFactory.getLog(MavenSearchEDTJob.class);

    private Runnable runnable;

    public MavenSearchEDTJob() {
        this(null);
    }

    public MavenSearchEDTJob(Runnable runnable) {
        super();
        this.runnable = runnable;
    }

    protected final void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public final int execute() throws Exception {
        if (this.runnable == null) {
            if (log.isWarnEnabled()) {
                log.warn("runnable is null!");
            }
            return 1;
        } else {
            this.runnable.run();
            return 0;
        }
    }
}
