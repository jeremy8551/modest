package cn.org.expect.concurrent.internal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import cn.org.expect.concurrent.EasyCombine;
import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.concurrent.EasyJobReader;
import cn.org.expect.concurrent.EasyJobService;
import cn.org.expect.concurrent.EasyJobWriter;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Terminator;
import cn.org.expect.util.TimeWatch;

/**
 * 接口的实现类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-04-12
 */
public class DefaultJobService extends Terminator implements EasyJobService {
    private final static Log log = LogFactory.getLog(DefaultJobService.class);

    /** 休眠的超时时间, 单位毫秒 */
    public static int SLEEP_TIMEOUT = 2 * 60 * 1000;

    /** 并发任务运行器的编号 */
    private final String id;

    /** 输入流 */
    private EasyJobReader reader;

    /** 任务错误信息输出流 */
    private EasyJobWriter writer;

    /** 同时运行线程的最大数 */
    private final int count;

    /** 已启动和正在运行的线程数量之和 */
    private AtomicInteger alive;

    /** 已启动线程的数量 */
    private AtomicInteger start;

    /** 发生错误线程的数量 */
    private AtomicInteger error;

    /** 线程池 */
    private final ExecutorService service;

    /** 线程协作 */
    private final EasyCombine combine = new EasyCombine();

    public DefaultJobService(String id, ExecutorService service, int count) {
        this.id = Ensure.notNull(id);
        this.service = Ensure.notNull(service);
        this.count = Ensure.fromOne(count);
        this.terminate = false;

        if (log.isDebugEnabled()) {
            log.debug("concurrent.stdout.message001", this.id);
        }
    }

    public void execute(List<? extends EasyJob> list) throws Exception {
        this.execute(new DefaultJobReader(list));
    }

    public void execute(EasyJobReader in) throws Exception {
        TimeWatch watch = new TimeWatch();
        DefaultJobWriter out = new DefaultJobWriter();
        int error = this.execute(in, out);
        if (error > 0) {
            throw out.toException(ResourcesUtils.getMessage("concurrent.stdout.message007", this.id, watch.useTime(), this.error.get()));
        }
    }

    public synchronized int execute(EasyJobReader in, EasyJobWriter out) throws Exception {
        this.reader = Ensure.notNull(in);
        if (log.isDebugEnabled()) {
            log.debug("concurrent.stdout.message003", this.id, this.count);
        }

        TimeWatch watch = new TimeWatch();
        this.writer = out;
        this.alive = new AtomicInteger(0);
        this.start = new AtomicInteger(0);
        this.error = new AtomicInteger(0);
        this.terminate = false;
        try {
            while (this.reader.hasNext() || this.alive.get() > 0) {
                if (this.terminate) {
                    break;
                }

                if (log.isDebugEnabled()) {
                    log.debug("concurrent.stdout.message002", this.id, this.alive.get(), this.reader.hasNext());
                }

                while (this.alive.get() < this.count) {
                    if (this.terminate) {
                        break;
                    }

                    if (!this.executeNext()) {
                        break; // 如果读取不到下一个任务
                    }
                }

                if (this.terminate) {
                    break;
                }

                int timeout = SLEEP_TIMEOUT; // 休眠时间, 单位毫秒
                if (log.isDebugEnabled()) {
                    log.debug("concurrent.stdout.message011", this.id, timeout);
                }

                // 因为是多线程并行计算，所以 alive 变量可能是一直在变化中，所以在休眠前再检查一下
                if (this.alive.get() > 0) {
                    this.combine.sleep(timeout);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("concurrent.stdout.message012", this.id);
                    }
                }
            }

            if (this.error.get() == 0) {
                if (log.isDebugEnabled()) {
                    log.debug("concurrent.stdout.message005", this.id);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("concurrent.stdout.message007", this.id, watch.useTime(), this.error.get());
                }
            }

            return this.error.get();
        } finally {
            this.reader = null;
            this.writer = null;
        }
    }

    /**
     * 启动一个并发任务
     *
     * @return 返回true表示成功启动一个并发任务，false表示没有待运行的任务
     */
    private boolean executeNext() throws Exception {
        if (this.reader.hasNext()) {
            EasyJob next = this.reader.next();
            if (next == null) {
                return this.executeNext();
            }

            if (log.isDebugEnabled()) {
                log.debug("concurrent.stdout.message004", next.getName(), this.service.toString());
            }

            DefaultRunnable task = new DefaultRunnable(this, next);
            this.service.submit(task);
            this.alive.incrementAndGet();
            this.start.incrementAndGet();
            return true;
        } else {
            return false;
        }
    }

    public void wakeup() {
        if (log.isDebugEnabled()) {
            log.debug("concurrent.stdout.message010", this.id);
        }
        this.combine.wakeup();
    }

    /**
     * 并发任务运行完毕，运行下一个任务
     */
    public void executeNextJob() {
        this.alive.decrementAndGet();// 运行任务数减一
        this.wakeup();
    }

    /**
     * 记录任务错误信息
     *
     * @param name    任务名
     * @param message 错误信息
     * @param e       异常信息
     */
    public void writeError(String name, String message, Exception e) {
        this.error.incrementAndGet();
        if (this.writer != null) {
            this.writer.addError(name, message, e);
        } else {
            if (log.isErrorEnabled()) {
                log.error(message, e);
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public int getConcurrency() {
        return this.count;
    }

    public int getAliveJob() {
        return this.alive.get();
    }

    public int getStartJob() {
        return this.start.get();
    }

    public int getErrorJob() {
        return this.error.get();
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.reader != null) {
            this.reader.terminate();
        }
    }
}
