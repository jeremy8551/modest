package cn.org.expect.concurrent.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.org.expect.collection.Throwables;
import cn.org.expect.concurrent.EasyJobWriter;

/**
 * 错误信息输出流
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-04-12
 */
public class DefaultJobWriter implements EasyJobWriter {

    /** 错误信息集合 */
    private final List<String> messages;

    /** 发生错误的并发任务名集合 */
    private final List<String> taskNames;

    /** 异常集合 */
    private final List<Exception> exceptionList;

    /**
     * 初始化
     */
    public DefaultJobWriter() {
        int size = 10;
        this.messages = new ArrayList<String>(size);
        this.taskNames = new ArrayList<String>(size);
        this.exceptionList = new ArrayList<Exception>(size);
    }

    public boolean hasError() {
        return !this.messages.isEmpty() || !this.taskNames.isEmpty() || !this.exceptionList.isEmpty();
    }

    public void addError(String name, String message, Exception e) {
        this.taskNames.add(name);
        this.messages.add(message);
        this.exceptionList.add(e);
    }

    /**
     * 清空
     */
    public void clear() {
        this.taskNames.clear();
        this.messages.clear();
        this.exceptionList.clear();
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    public List<String> getNames() {
        return Collections.unmodifiableList(this.taskNames);
    }

    public List<Exception> getThrowables() {
        return Collections.unmodifiableList(this.exceptionList);
    }

    /**
     * 转为异常信息
     *
     * @param message 错误信息
     * @return 异常信息
     */
    public Exception toException(String message) {
        Throwables list = new Throwables(message);
        for (int i = 0; i < this.taskNames.size(); i++) {
            String msg = this.messages.get(i);
            Throwable e = this.exceptionList.get(i);
            list.add(msg, e);
        }
        return list;
    }
}
