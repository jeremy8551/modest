package cn.org.expect.log;

import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.PatternLog;
import org.junit.Assert;
import org.junit.Test;

public class AbstractLoggerTest {

    @Test
    public void test0() {
        String fqcn = null;

        try {
            new PatternLog(null, AbstractLoggerTest.class, LogLevel.OFF, fqcn, false);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        try {
            new PatternLog(new LogContextImpl(), null, LogLevel.TRACE, fqcn, false);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        try {
            new PatternLog(new LogContextImpl(), AbstractLoggerTest.class, null, fqcn, false);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void test1() {
        String fqcn = null;
        PatternLog log = new PatternLog(new LogContextImpl(), AbstractLoggerTest.class, LogLevel.OFF, fqcn, false);

        log.setLevel(LogLevel.TRACE);
        Assert.assertTrue(log.isTraceEnabled());

        log.setLevel(LogLevel.DEBUG);
        Assert.assertTrue(log.isDebugEnabled());

        log.setLevel(LogLevel.INFO);
        Assert.assertTrue(log.isInfoEnabled());

        log.setLevel(LogLevel.WARN);
        Assert.assertTrue(log.isWarnEnabled());

        log.setLevel(LogLevel.ERROR);
        Assert.assertTrue(log.isErrorEnabled());

        log.setLevel(LogLevel.FATAL);
        Assert.assertTrue(log.isFatalEnabled());

        try {
            log.setLevel(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }
}
