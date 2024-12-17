package cn.org.expect.log;

import cn.org.expect.log.apd.DefaultLogTest;
import org.junit.Test;

/**
 * 测试非嵌套日志的场景（代理类 LogProxy 没有继承 AbstractLogger 或 LevelLogger等）
 */
public class FqcnTest2 {

    @Test
    public void test() throws Exception {
        LogFactory.load("sout+");
        Log log = LogFactory.getLog(DefaultLogTest.class);
        LogFactory.setFQCN(log, "^" + LogProxy.class.getName());
        Log target = new LogProxy(log);
        System.out.println(target.getClass().getName());

        target.info("a.b");
        target.info("a.b\ncde");
        target.info("a.b.c.d", "1", "2");
        target.info("a.b.c.d.e", "1\n2\n34\n5");
        target.info("test.no.key", "noKey");
    }

    public static class LogProxy implements Log {

        private final Log target; // 目标对象

        public LogProxy(Log target) {
            this.target = target;
        }

        public String getName() {
            return "";
        }

        public boolean isTraceEnabled() {
            return true;
        }

        public boolean isDebugEnabled() {
            return true;
        }

        public boolean isInfoEnabled() {
            return true;
        }

        public boolean isWarnEnabled() {
            return true;
        }

        public boolean isErrorEnabled() {
            return true;
        }

        public boolean isFatalEnabled() {
            return true;
        }

        public void trace(String message, Object... args) {
            this.target.trace(message, args);
        }

        public void trace(String message, Throwable e) {
            this.target.trace(message, e);
        }

        public void debug(String message, Object... args) {
            this.target.debug(message, args);
        }

        public void debug(String message, Throwable e) {
            this.target.debug(message, e);
        }

        public void info(String message, Object... args) {
            this.target.info(message, args);
        }

        public void info(String message, Throwable e) {
            this.target.info(message, e);
        }

        public void warn(String message, Object... args) {
            this.target.warn(message, args);
        }

        public void warn(String message, Throwable e) {
            this.target.warn(message, e);
        }

        public void error(String message, Object... args) {
            this.target.error(message, args);
        }

        public void error(String message, Throwable e) {
            this.target.error(message, e);
        }

        public void fatal(String message, Object... args) {
            this.target.fatal(message, args);
        }

        public void fatal(String message, Throwable e) {
            this.target.fatal(message, e);
        }
    }
}
