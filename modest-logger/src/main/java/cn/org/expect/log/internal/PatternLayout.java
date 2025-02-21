package cn.org.expect.log.internal;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.Layout;
import cn.org.expect.log.LogEvent;
import cn.org.expect.log.LogField;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 日志格式
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class PatternLayout implements Layout {

    /** 缓存 */
    private final StringBuilder buf;

    /** 临时集合 */
    private final List<String> list;

    /** 日志格式 */
    private final LogPattern pattern;

    public PatternLayout(String pattern) {
        this.buf = new StringBuilder(100);
        this.list = new ArrayList<String>();
        this.pattern = new LogPattern(pattern);
    }

    /**
     * 初始化一个日志信息
     *
     * @param event 日志事件
     * @return 日志记录
     */
    public synchronized String format(LogEvent event) {
        this.buf.setLength(0);
        String message = event.getMessage();
        if (StringUtils.contains(message, '\r', '\n')) {
            this.list.clear();
            StringUtils.splitLines(message, this.list);
            this.addRows(event, this.list, this.buf);
        } else {
            this.addRow(event, this.buf);
        }
        return this.buf.toString();
    }

    /**
     * 将日志信息按多行输出
     *
     * @param event 日志事件
     * @param list  日志信息集合
     * @param buf   缓冲区
     */
    protected void addRows(LogEvent event, List<String> list, StringBuilder buf) {
        String message = event.getMessage();
        Throwable throwable = event.getThrowable();

        try {
            int size = list.size() - 1;
            for (int i = 0; i < size; i++) {
                String line = list.get(i);
                event.setMessage(line);
                event.setThrowable(null);
                this.addRow(event, buf);

                if (!this.pattern.hasNewLine()) {
                    buf.append(Settings.getLineSeparator());
                }
            }

            // 在最后一行输出异常信息
            event.setMessage(list.get(size));
            event.setThrowable(throwable);
            this.addRow(event, buf);
        } finally {
            event.setMessage(message);
            event.setThrowable(throwable);
        }
    }

    /**
     * 将日志信息按一行输出
     *
     * @param event 日志事件
     * @param buf   缓冲区
     */
    protected void addRow(LogEvent event, StringBuilder buf) {
        List<LogField> list = this.pattern.getFields();
        for (int i = 0; i < list.size(); i++) {
            buf.append(list.get(i).format(event));
        }
    }

    public String getPattern() {
        return this.pattern.getName();
    }
}
