package cn.org.expect.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import cn.org.expect.util.Logs;
import cn.org.expect.util.StackTraceUtils;

/**
 * 让 JUL 日志准确输出代码位置
 */
public class JULHandler extends Handler {

    protected JULHandler() {
        super();
    }

    /**
     * 初始化
     */
    public static void reset(Logger log) {
        Handler[] array = log.getHandlers();

        // 移除所有
        for (Handler handler : array) {
            log.removeHandler(handler);
        }

        // 将自定义的handler放在第一位
        log.addHandler(new JULHandler());

        // 重新添加
        for (Handler handler : array) {
            log.addHandler(handler);
        }
    }

    public void publish(LogRecord record) {
        StackTraceElement trace = StackTraceUtils.get(Logs.class.getName());
        record.setLoggerName(trace.getClassName());
        record.setSourceClassName(trace.getClassName() + ".java:" + trace.getLineNumber());
        record.setSourceMethodName(trace.getMethodName() + "()");
    }

    public void flush() {
    }

    public void close() {
    }
}
