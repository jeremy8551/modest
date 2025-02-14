package cn.org.expect.log.file;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import cn.org.expect.log.Appender;
import cn.org.expect.log.Console;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogEvent;
import cn.org.expect.log.internal.PatternConsoleAppender;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ShutdownHook;

/**
 * 文件记录器
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/23
 */
public class FileAppender extends PatternConsoleAppender {

    /** 日志文件 */
    private String file;

    /** 日志文件字符集 */
    private String charsetName;

    /** 输出流 */
    private FileAppenderWriter out;

    /** 提交事物 */
    private FileAppenderJob job;

    /** 缓冲区长度 */
    private final int size;

    /** true表示追加写入日志，false表示覆盖日志文件 */
    private final boolean append;

    /** 线程池 */
    private final Executor service;

    public FileAppender(String logfile, String charsetName, String pattern, boolean append) {
        this(null, logfile, charsetName, pattern, 300, append);
    }

    public FileAppender(Executor service, String logfile, String charsetName, String pattern, int size, boolean append) {
        super(pattern);
        this.file = logfile;
        this.charsetName = charsetName;
        this.service = service;
        this.size = Ensure.fromOne(size);
        this.append = append;
    }

    public String getName() {
        return FileAppender.class.getSimpleName();
    }

    /**
     * 设置日志文件绝对路径
     *
     * @param logfile 日志文件绝对路径
     */
    public FileAppender logfile(String logfile) {
        this.file = logfile;
        return this;
    }

    /**
     * 设置文件的字符集
     *
     * @param charsetName 字符集
     */
    public FileAppender charsetName(String charsetName) {
        this.charsetName = charsetName;
        return this;
    }

    /**
     * 返回文件绝对路径
     *
     * @return 文件路径
     */
    public String getFile() {
        return this.out.getFile();
    }

    public FileAppender pattern(String pattern) {
        super.pattern(pattern);
        return this;
    }

    public Appender setup(LogContext context) {
        try {
            if (this.service == null) {
                this.out = new FileAppenderWriter(this.file, this.charsetName, new SimpleBlockingQueue<LogEvent>(), this.layout, this.append);
            } else {
                this.out = new FileAppenderWriter(this.file, this.charsetName, new LinkedBlockingQueue<LogEvent>(this.size), this.layout, this.append);
                this.job = new FileAppenderJob(this.out);
                this.service.execute(this.job);
            }

            // 注册挂钩线程
            ShutdownHook.register(new Callable<String>() {
                public String call() {
                    close();
                    return "";
                }
            });

            // 安装记录器
            context.addAppender(this);
            return this;
        } catch (Throwable e) {
            Console.out.error(this.file, e);
            return null;
        }
    }

    public synchronized void append(LogEvent event) {
        if (this.out != null) {
            this.out.add(event);

            // 如果是单线程记录日志
            if (this.service == null) {
                try {
                    this.out.write();
                } catch (IOException e) {
                    Console.out.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    public synchronized void close() {
        if (this.job != null) {
            this.job.terminate();
            this.job.waitFor();
            this.job = null;
        }

        if (this.out != null) {
            this.out.close();
            this.out = null;
        }
    }
}
