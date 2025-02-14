package cn.org.expect.log.file;

import java.util.concurrent.TimeUnit;

import cn.org.expect.log.Console;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Terminator;

/**
 * 记录日志任务
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/25
 */
public class FileAppenderJob extends Terminator implements Runnable {

    /** 文件输出流 */
    private final FileAppenderWriter out;

    /** 线程是否正在运行 */
    private volatile boolean running;

    public FileAppenderJob(FileAppenderWriter out) {
        super();
        this.out = Ensure.notNull(out);
        this.running = false;
        this.terminate = false;
    }

    public void run() {
        this.running = true;
        this.terminate = false;
        try {
            while (!this.terminate) {
                this.out.write();
            }
            this.out.flush();
        } catch (Throwable e) {
            Console.out.error(e.getLocalizedMessage(), e);
        } finally {
            this.running = false;
        }
    }

    /**
     * 让调用方法的线程进入等待状态，等待日志记录线程退出
     */
    public void waitFor() {
        while (this.running) {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                Console.out.debug(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * 终止运行
     */
    public void terminate() {
        this.terminate = true;
        this.out.unlock();
    }
}
