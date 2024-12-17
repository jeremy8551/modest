package cn.org.expect.log;

import cn.org.expect.log.apd.DefaultLogTest;
import cn.org.expect.util.ClassUtils;
import org.junit.Test;

/**
 * 测试嵌套日志的场景（代理类 LogProxy 没有继承 AbstractLogger 或 LevelLogger等）
 */
public class FqcnTest1 {

    @Test
    public void test() throws Exception {
        LogFactory.load("sout+");
        Log log = LogFactory.getLog(DefaultLogTest.class);

        if (log instanceof FqcnAware) {
            ((FqcnAware) log).setFqcn("^" + LevelLogger.class.getName());
        }

        Log target = new LogProxy(LogFactory.getContext(), log);
        System.out.println(target.getClass().getName());

        target.info("a.b");
        target.info("a.b\ncde");
        target.info("a.b.c.d", "1", "2");
        target.info("a.b.c.d.e", "1\n2\n34\n5");
        target.info("test.no.key", "noKey");
    }

    public static class LogProxy extends AbstractLogger {

        private final Log target; // 目标对象

        public LogProxy(LogContext context, Log target) {
            super(context, ClassUtils.forName(target.getName()), LogLevel.INFO);
            this.target = target;
        }

        @Override
        public void printFatal(String message, Throwable e) {
            this.target.fatal(message, e);
        }

        @Override
        public void printFatal(String message, Object... args) {
            this.target.fatal(message, args);
        }

        @Override
        public void printError(String message, Throwable e) {
            this.target.error(message, e);
        }

        @Override
        public void printError(String message, Object... args) {
            this.target.error(message, args);
        }

        @Override
        public void printWarn(String message, Throwable e) {
            this.target.warn(message, e);
        }

        @Override
        public void printWarn(String message, Object... args) {
            this.target.warn(message, args);
        }

        @Override
        public void printInfo(String message, Throwable e) {
            this.target.info(message, e);
        }

        @Override
        public void printInfo(String message, Object... args) {
            this.target.info(message, args);
        }

        @Override
        public void printDebug(String message, Throwable e) {
            this.target.debug(message, e);
        }

        @Override
        public void printDebug(String message, Object... args) {
            this.target.debug(message, args);
        }

        @Override
        public void printTrace(String message, Throwable e) {
            this.target.trace(message, e);
        }

        @Override
        public void printTrace(String message, Object... args) {
            this.target.trace(message, args);
        }
    }
}
