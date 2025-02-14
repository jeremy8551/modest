package cn.org.expect.log;

import java.io.File;
import java.io.IOException;

import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.PatternConsoleAppender;
import cn.org.expect.log.internal.PatternLogBuilder;
import cn.org.expect.log.slf4j.Slf4jLogBuilder;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class LogConfigAnalysisTest {

    @Test
    public void test1() {
        LogContext context = new LogContextImpl();
        Assert.assertEquals(0, new LogSettings(context).load("sout:trace").length);
        Assert.assertEquals(PatternLogBuilder.class, context.getBuilder().getClass());

        Log log = LogFactory.getLog(context, LogConfigAnalysisTest.class, null, false);
        Assert.assertTrue(log.isTraceEnabled());
        Assert.assertTrue(log.isDebugEnabled());
        Assert.assertTrue(log.isInfoEnabled());
        Assert.assertTrue(log.isWarnEnabled());
        Assert.assertTrue(log.isErrorEnabled());
        Assert.assertTrue(log.isFatalEnabled());

        PatternConsoleAppender appender = context.findAppender(PatternConsoleAppender.class);
        Assert.assertNotNull(appender);
        Assert.assertEquals(LogFactory.SOUT_PATTERN, appender.getPattern());
    }

    @Test
    public void test2() {
        System.setProperty(LogFactory.PROPERTY_LOG_SOUT, "");
        LogContext context = new LogContextImpl();
        Assert.assertEquals(0, new LogSettings(context).load("error:sout+").length);
        Assert.assertEquals(PatternLogBuilder.class, context.getBuilder().getClass());

        Log log = LogFactory.getLog(context, LogConfigAnalysisTest.class, null, false);
        Assert.assertFalse(log.isTraceEnabled());
        Assert.assertFalse(log.isDebugEnabled());
        Assert.assertFalse(log.isInfoEnabled());
        Assert.assertFalse(log.isWarnEnabled());
        Assert.assertTrue(log.isErrorEnabled());

        PatternConsoleAppender appender = context.findAppender(PatternConsoleAppender.class);
        Assert.assertNotNull(appender);
        Assert.assertEquals(LogFactory.SOUT_PLUS_PATTERN, appender.getPattern());
    }

    @Test
    public void test3() {
        LogContext context = new LogContextImpl();
        Assert.assertEquals(1, new LogSettings(context).load("slf4j|info").length);
    }

    @Test
    public void test4() {
        LogContext context = new LogContextImpl();
        Assert.assertEquals(0, new LogSettings(context).load("slf4j:").length);
        Assert.assertEquals(Slf4jLogBuilder.class, context.getBuilder().getClass());
    }

    @Test
    public void test5() {
        try {
            LogContext context = new LogContextImpl();
            new LogSettings(context).load("sout:sout+");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(e.getLocalizedMessage(), "sout:sout+");
        }
    }

    /**
     * 测试覆盖文件方式写入日志
     */
    @Test
    public void test6() throws IOException {
        File logfile = FileUtils.createTempFile("testLog.log");
        String charsetName = Settings.getFileEncoding();
        String content = "testLogFilePath";

        FileUtils.assertWrite(logfile, charsetName, false, content);
        Assert.assertEquals(content, FileUtils.readline(logfile, charsetName, 0));

        LogContext context = new LogContextImpl();
        context.removeAppender(PatternConsoleAppender.class);
        Assert.assertEquals(0, new LogSettings(context).load(">" + logfile).length);
        Log log = LogFactory.getLog(context, this.getClass());

        log.info("");
        Assert.assertEquals("", StringUtils.trimBlank(FileUtils.readline(logfile, charsetName, 0)));
    }

    /**
     * 测试追加方式写入日志
     */
    @Test
    public void test7() throws IOException {
        File logfile = FileUtils.createTempFile("testLog.log");
        String charsetName = Settings.getFileEncoding();
        String content = "testLogFilePath";

        FileUtils.assertWrite(logfile, charsetName, false, content);
        Assert.assertEquals(content, FileUtils.readline(logfile, charsetName, 0));

        LogContext context = new LogContextImpl();
        context.removeAppender(PatternConsoleAppender.class);
        Assert.assertEquals(0, new LogSettings(context).load(">>" + logfile).length);
        Log log = LogFactory.getLog(context, this.getClass());

        log.info("ceshi");
        Assert.assertEquals(content + "ceshi", StringUtils.trimBlank(FileUtils.readline(logfile, charsetName, 0)));
    }

    /**
     * 测试是否能
     */
    @Test
    public void test8() {
        LogContext context = new LogContextImpl();
        Assert.assertEquals(0, new LogSettings(context).load("sout+:info").length);
        Log log = LogFactory.getLog(context, this.getClass());

        Assert.assertFalse(log.isDebugEnabled());
        Assert.assertTrue(log.isInfoEnabled());
        Assert.assertEquals(0, new LogSettings(context).load("error", "debug").length);
        Assert.assertTrue(log.isDebugEnabled());
    }

    @Test
    public void test9() throws IOException {
        File logfile = FileUtils.createTempFile("testLog.log");
        String charsetName = Settings.getFileEncoding();

        LogContext context = new LogContextImpl();
        context.removeAppender(PatternConsoleAppender.class);
        Assert.assertEquals(0, new LogSettings(context).load(">>" + logfile + "+").length);
        Log log = LogFactory.getLog(context, this.getClass());

        String str = "ceshi";
        log.info(str);

        String content = FileUtils.readline(logfile, charsetName, 0);
        log.info(content);

        String[] array = StringUtils.split(content, '|');
        Assert.assertEquals(12, array[0].length()); // 第一个字段是时间，如：19:07:33:138
        Assert.assertTrue(LogLevel.is(array[1])); // 第二个字段是：日志级别
        Assert.assertEquals(str, StringUtils.trimBlank(ArrayUtils.last(array)));
    }

    @Test
    public void test10() throws IOException {
        File logfile = FileUtils.createTempFile("testLog.log");
        String charsetName = Settings.getFileEncoding();

        LogContext context = new LogContextImpl();
        context.removeAppender(PatternConsoleAppender.class);
        context.setBuilder(new PatternLogBuilder());
        Assert.assertEquals(0, new LogSettings(context).load(">>" + logfile + "+%-5.5p|%d|%30.30c|%50.50l|%m%ex%n").length);

        Log log = LogFactory.getLog(context, this.getClass());
        String str = "ceshi";
        log.info(str);

        String content = FileUtils.readline(logfile, charsetName, 0);
        String[] array = StringUtils.split(content, '|');
        Assert.assertTrue(LogLevel.is(array[0])); // 第二个字段是：日志级别
        Assert.assertEquals(12, array[1].length()); // 第一个字段是时间，如：19:07:33:138
        Assert.assertEquals(str, StringUtils.trimBlank(ArrayUtils.last(array)));
    }
}
