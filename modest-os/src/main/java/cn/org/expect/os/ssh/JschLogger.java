package cn.org.expect.os.ssh;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import com.jcraft.jsch.Logger;

/**
 * Jsch日志的适配器
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-03-22
 */
public class JschLogger implements Logger {

    private final Log log;

    public JschLogger() {
        this.log = LogFactory.getLog(LogFactory.getContext(), this.getClass(), JschLogger.class.getName(), true);
    }

    public boolean isEnabled(int level) {
        switch (level) {
            case DEBUG:
            case INFO:
            case WARN:
                return log.isDebugEnabled();

            case ERROR:
                return log.isErrorEnabled();

            case FATAL:
                return log.isFatalEnabled();

            default:
                throw new UnsupportedOperationException(String.valueOf(level));
        }
    }

    public void log(int level, String message) {
        switch (level) {
            case DEBUG:
            case INFO:
            case WARN:
                if (log.isDebugEnabled()) {
                    log.debug(message);
                }
                break;

            case ERROR:
                if (log.isErrorEnabled()) {
                    log.error(message);
                }
                break;

            case FATAL:
                if (log.isFatalEnabled()) {
                    log.fatal(message);
                }
                break;

            default:
                throw new UnsupportedOperationException(String.valueOf(level));
        }
    }
}
