package cn.org.expect.printer;

import java.io.IOException;
import java.io.Writer;
import java.text.Format;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 信息输出接口的默认实现类
 *
 * @author jeremy8551@qq.com
 */
public class StandardPrinter implements Printer, java.io.Closeable {

    /** 日志接口 */
    protected Log log;

    /** 多任务程序使用的缓存 */
    protected LinkedHashMap<String, CharSequence> multipleTask;

    /** 缓存，每次输出信息之前需要清空缓存 */
    protected StringBuilder buffer;

    /** 信息输出流 */
    protected Writer writer;

    /** 类型转换器（负责将 Object 对象转为字符串） */
    protected Format converter;

    /** 输出锁 */
    private final Object printLock = new Object();

    /** 输出锁 */
    private final Object multipleLock = new Object();

    /**
     * 初始化
     */
    public StandardPrinter() {
        this.log = LogFactory.getLog(LogFactory.getContext(), this.getClass(), StandardPrinter.class.getName(), false);
        this.buffer = new StringBuilder(100);
        this.multipleTask = new LinkedHashMap<String, CharSequence>();
    }

    /**
     * 初始化
     *
     * @param writer    信息输出接口, 为 null 时默认使用 {@linkplain Log} 接口输出信息
     * @param converter 类型转换器(用于将 Object 对象转为字符串, 为 null 时默认使用 {@linkplain Object#toString()})
     */
    public StandardPrinter(Writer writer, Format converter) {
        this();
        this.setWriter(writer);
        this.setFormatter(converter);
    }

    /**
     * 初始化
     *
     * @param writer 信息输出接口, 为 null 时默认使用 {@linkplain Log} 接口输出信息
     */
    public StandardPrinter(Writer writer) {
        this();
        this.setWriter(writer);
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void print(Object object) {
        this.print(this.converter == null ? String.valueOf(object) : this.converter.format(object));
    }

    public void print(CharSequence cs) {
        synchronized (this.printLock) {
            this.buffer.append(cs);
        }
    }

    public void println(CharSequence msg) {
        synchronized (this.printLock) {
            this.buffer.append(msg);

            if (this.writer != null) {
                try {
                    this.buffer.append(FileUtils.lineSeparator);
                    IO.write(this.writer, this.buffer);
                    this.buffer.setLength(0);
                    this.writer.flush();
                    return;
                } catch (Throwable e) {
                    if (log.isErrorEnabled()) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info(this.buffer.toString());
                }
                this.buffer.setLength(0);
                return;
            }
        }
    }

    public void println(String id, CharSequence msg) {
        synchronized (this.multipleLock) {
            this.multipleTask.put(id, msg); // 保存某个任务信息
        }

        Set<String> keys = this.multipleTask.keySet();
        StringBuilder buf = new StringBuilder(keys.size() * 30);
        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) { // 保存某个任务信息后，重新生成最新的任务信息
            String taskId = it.next();
            buf.append(StringUtils.escapeLineSeparator(this.multipleTask.get(taskId)));
            if (it.hasNext()) {
                buf.append(FileUtils.lineSeparator);
            }
        }
        this.println(buf);
    }

    public void println() {
        this.println("");
    }

    public void println(Object object) {
        this.println(this.converter == null ? String.valueOf(object) : this.converter.format(object));
    }

    public void println(CharSequence msg, Throwable e) {
        StringBuilder buf = new StringBuilder(msg.length() + 100);
        buf.append(msg);
        buf.append(FileUtils.lineSeparator);
        buf.append(StringUtils.toString(e));

        if (this.writer != null) {
            try {
                buf.append(FileUtils.lineSeparator);
                IO.write(this.writer, buf);
                this.writer.flush();
            } catch (IOException e1) {
                if (log.isErrorEnabled()) {
                    log.error(e1.getLocalizedMessage(), e1);
                }
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error(buf.toString());
            }
        }
    }

    public void flush() {
        if (this.writer == null) {
            return;
        }

        if (this.buffer.length() > 0) {
            try {
                IO.write(this.writer, this.buffer);
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getLocalizedMessage(), e);
                }
            }
            this.buffer.setLength(0);
        }
        IO.flushQuiet(this.writer);
    }

    public void close() {
        this.flush();
        IO.close(this.writer);
        this.multipleTask.clear();
        this.buffer = new StringBuilder(100);
    }

    public void setFormatter(Format converter) {
        this.converter = converter;
    }

    public Format getFormatter() {
        return this.converter;
    }

    public String toString() {
        return StandardPrinter.class.getSimpleName() + "[mulityTask=" + StringUtils.toString(this.multipleTask) + ", writer=" + writer + "]";
    }
}
