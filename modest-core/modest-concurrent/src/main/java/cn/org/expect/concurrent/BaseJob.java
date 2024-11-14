package cn.org.expect.concurrent;

/**
 * 并发任务模版
 *
 * @author jeremy8551@qq.com
 * @createtime 2024/11/15
 */
public abstract class BaseJob implements EasyJob, Terminate {

    /** 任务的终止状态 */
    protected volatile boolean terminate;

    /** 任务名 */
    private String name;

    public abstract int execute() throws Exception;

    public void terminate() {
        this.terminate = true;
    }

    public boolean isTerminate() {
        return this.terminate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
