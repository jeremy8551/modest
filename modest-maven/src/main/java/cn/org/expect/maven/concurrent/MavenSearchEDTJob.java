package cn.org.expect.maven.concurrent;

public class MavenSearchEDTJob extends MavenSearchJob implements EDTJob {

    private final Runnable runnable;

    public MavenSearchEDTJob(Runnable runnable) {
        super();
        this.runnable = runnable;
    }

    public int execute() throws Exception {
        runnable.run();
        return 0;
    }
}
