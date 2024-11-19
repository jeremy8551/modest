package cn.org.expect.maven.concurrent;

import cn.org.expect.util.Ensure;

public class MavenSearchEDTJob extends MavenSearchJob implements EDTJob {

    private final Runnable runnable;

    public MavenSearchEDTJob(Runnable runnable) {
        super();
        this.runnable = Ensure.notNull(runnable);
    }

    public int execute() throws Exception {
        runnable.run();
        return 0;
    }
}
