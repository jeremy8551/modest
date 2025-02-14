package cn.org.expect.concurrent;

import cn.org.expect.util.Terminator;

/**
 * 并发任务模版
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/11/15
 */
public abstract class BaseJob extends Terminator implements EasyJob {

    /** 任务名 */
    private String name;

    public abstract int execute() throws Exception;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
