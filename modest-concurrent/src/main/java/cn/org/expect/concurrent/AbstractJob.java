package cn.org.expect.concurrent;

import cn.org.expect.util.Terminates;

/**
 * 并发任务模版
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/29
 */
public abstract class AbstractJob implements EasyJob {

    /** 任务终止状态 */
    protected final Terminates status = new Terminates();

    /** 任务名 */
    private String name;

    public abstract int execute() throws Exception;

    public void terminate() throws Exception {
        this.status.terminate();
    }

    public boolean isTerminate() {
        return this.status.isTerminate();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
