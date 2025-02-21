package cn.org.expect.collection;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 存储多个异常
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/26
 */
public class Throwables extends RuntimeException {

    /** 异常集合 */
    private final List<String> messages;

    /** 异常错误信息 */
    private final List<Throwable> list;

    /**
     * 异常集合
     */
    public Throwables() {
        this("");
    }

    /**
     * 异常集合
     *
     * @param message 异常信息
     */
    public Throwables(String message) {
        super(message);
        this.messages = new ArrayList<String>();
        this.list = new ArrayList<Throwable>();
    }

    /**
     * 添加异常信息
     *
     * @param message 信息
     * @param e       异常信息
     */
    public void add(String message, Throwable e) {
        this.list.add(e);
        this.messages.add(message);
    }

    /**
     * 添加异常信息
     *
     * @param e 异常信息
     */
    public void add(Throwable e) {
        this.add(e.getLocalizedMessage(), e);
    }

    /**
     * 返回集合中异常个数
     *
     * @return 集合元素个数
     */
    public int size() {
        return this.messages.size();
    }

    /**
     * 返回true表示不为空
     *
     * @return 返回true表示集合不是空的 返回false表示集合是空的
     */
    public boolean notEmpty() {
        return !this.messages.isEmpty();
    }

    public void printStackTrace(PrintStream stream) {
        int size = this.messages.size();
        for (int i = 0; i < size; i++) {
            stream.println(this.messages.get(i));
            this.list.get(i).printStackTrace(stream);
        }
    }

    public void printStackTrace(PrintWriter writer) {
        int size = this.messages.size();
        for (int i = 0; i < size; i++) {
            writer.println(this.messages.get(i));
            this.list.get(i).printStackTrace(writer);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        int size = this.messages.size();
        for (int i = 0; i < size; i++) {
            buf.append(Settings.getLineSeparator());
            buf.append(StringUtils.joinLineSeparator(this.messages.get(i), StringUtils.toString(this.list.get(i))));
        }
        return StringUtils.trimBlank(buf);
    }
}
