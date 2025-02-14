package cn.org.expect.time;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;

/**
 * 用于监视任务是否超时,如果任务A超时自动调用任务A的 termiante函数终止任务
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-05
 */
public class TimeoutMonitor extends TimerTask {
    private final static Log log = LogFactory.getLog(TimeoutMonitor.class);

    /**
     * 被监视任务
     */
    private final TimerTask target;

    /**
     * 初始化
     *
     * @param task 被监视任务
     */
    public TimeoutMonitor(TimerTask task) {
        super();
        this.target = task;
        this.setTimeout(0);
        this.setQueue(null);
        this.setTaskId(TimeoutMonitor.toMonitorTaskId(task.getTaskId()));
        this.setSchedule(Timer.SCHEDULE_DELAY);
        this.setDelay(task.getTimeout());
    }

    public void execute() throws Exception {
        if (target != null && target.isRunning()) {
            long second = getDelay() / 1000;
            if (log.isWarnEnabled()) {
                log.warn("timer.stdout.message031", target.getTaskId(), second);
            }
            target.terminate();
        }
    }

    public void terminate() {
        throw new UnsupportedOperationException();
    }

    /**
     * 生成超时监控任务id
     *
     * @param taskId 被监控任务id
     * @return 任务id
     */
    public static String toMonitorTaskId(String taskId) {
        return taskId + "@MONITOR@TIMEOUT";
    }
}
