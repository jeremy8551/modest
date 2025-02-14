package cn.org.expect.log;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.apd.DefaultLogTest;
import cn.org.expect.log.internal.LogBuilderAppender;
import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.PatternConsoleAppender;
import cn.org.expect.log.internal.PatternLogBuilder;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试嵌套日志的场景（代理类 LogProxy 没有继承 AbstractLogger 或 LevelLogger等）
 */
public class FqcnTest {

    @Test
    public void test() throws Exception {
        LogContext context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());
        context.removeAppender(PatternConsoleAppender.class);
        Appender appender = new LogBuilderAppender(LogFactory.SOUT_PLUS_PATTERN).setup(context);

        Log log = LogFactory.getLog(context, DefaultLogTest.class);
        LogFactory.setFQCN(log, "^" + LevelLogger.class.getName());

        LogProxy target = new LogProxy(context, log);
        target.info("a.b");
        target.info("a.b\ncde");
        target.info("a.b.c.d", "1", "2");
        target.info("a.b.c.d.e", "1\n2\n34\n5");
        target.info("test.no.key", "noKey");

        String str = appender.toString();
        List<String> arrayList = new ArrayList<String>();
        List<String> list = StringUtils.splitLines(str, new ArrayList<String>());
        for (String line : list) {
            String[] array = StringUtils.split(line, '|');
            if (array.length == 5) {
                arrayList.add(array[4]);
            }
        }

        Assert.assertEquals("test ab", arrayList.get(0));
        Assert.assertEquals("a.b", arrayList.get(1));
        Assert.assertEquals("cde", arrayList.get(2));
        Assert.assertEquals("1 2", arrayList.get(3));
        Assert.assertEquals("1", arrayList.get(4));
        Assert.assertEquals("2", arrayList.get(5));
        Assert.assertEquals("34", arrayList.get(6));
        Assert.assertEquals("5", arrayList.get(7));
        Assert.assertEquals("test.no.key", arrayList.get(8));
    }

    private static class LogProxy extends AbstractLogger {

        // 目标对象
        private final Log target;

        public LogProxy(LogContext context, Log target) {
            super(context, ClassUtils.forName(target.getName()), LogLevel.INFO);
            this.target = target;
        }

        public void fatal(String message, Throwable cause) {
            this.target.fatal(message, cause);
        }

        public void error(String message, Throwable cause) {
            this.target.error(message, cause);
        }

        public void warn(String message, Throwable cause) {
            this.target.warn(message, cause);
        }

        public void info(String message, Throwable cause) {
            this.target.info(message, cause);
        }

        public void debug(String message, Throwable cause) {
            this.target.debug(message, cause);
        }

        public void trace(String message, Throwable cause) {
            this.target.trace(message, cause);
        }
    }
}
