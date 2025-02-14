package cn.org.expect.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.file.FileAppender;
import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.PatternConsoleAppender;
import cn.org.expect.log.internal.PatternLogBuilder;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class LogTest {

    @Test
    public void test2() throws IOException {
        File logfile = FileUtils.createTempFile("LogTest_test2.log");

        String pattern = "-------|%%|%d{yyyy-MM-dd HH:mm:ss.SSS}|%-5.5p|%-5.5level|%processId|%t|%l|%c|%C.%M(%F:%L)|mills=%r|%X{test}|%m%ex%n";
        LogContext context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());
        context.findAppender(PatternConsoleAppender.class).pattern(pattern);
        Assert.assertNotNull(new FileAppender(logfile.getAbsolutePath(), CharsetName.UTF_8, pattern, true).setup(context));
        context.removeAppender(PatternConsoleAppender.class);

        String str = "\r";
        str += "1\n";
        str += "12\r\n";
        str += "\n";
        str += "1345\r\n";
        str += "\n";

        String value = "testValue";
        MDC.put("test", value);

        Log log = LogFactory.getLog(context, LogTest.class);
        log.info(str);

        String content = FileUtils.readline(logfile, CharsetName.UTF_8, 0);
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(content, list);
        Assert.assertEquals(6, list.size());
        for (String line : list) {
            String[] array = StringUtils.split(line, '|');
            Assert.assertEquals("-------", array[0]);
            Assert.assertEquals("%", array[1]);
            Assert.assertNotNull(Dates.parse(array[2]));
            Assert.assertEquals("INFO ", array[3]);
            Assert.assertEquals("INFO ", array[4]);
            Integer.parseInt(array[5]);
            Assert.assertTrue(StringUtils.isNotBlank(array[6]));
            Assert.assertEquals(LogTest.class.getName(), array[8]);
            Assert.assertEquals(value, array[11]);
        }
    }

    @Test
    public void test3() throws IOException, InterruptedException {
        File logfile = FileUtils.createTempFile("LogTest_test3.log");

        String pattern = "-------|%%|%d{yyyy-MM-dd HH:mm:ss.SSS}|%-5.5p|%-5.5level|%processId|%t|%l|%c|%C.%M(%F:%L)|%r|%X{test}|%m%ex|%c{2}|%c{0}|%c{-2}%n";
        LogContext context = new LogContextImpl();
        context.updateLevel("", LogLevel.TRACE);
        context.setBuilder(new PatternLogBuilder());
        context.findAppender(PatternConsoleAppender.class).pattern(pattern);
        Assert.assertNotNull(new FileAppender(logfile.getAbsolutePath(), CharsetName.UTF_8, pattern, true).setup(context));
        context.removeAppender(PatternConsoleAppender.class);

        String testValue = "testValue";
        MDC.put("test", testValue);
        Log log = LogFactory.getLog(context, LogTest.class);
        log.trace("test level is {}", "trace");
        log.debug("test level is {}", "debug");
        log.info("test level is {}", "info");
        log.error("test level is {}", "error");
        log.fatal("test level is {}", "fatal");
        MDC.clear();

        String content = FileUtils.readline(logfile, CharsetName.UTF_8, 0);
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(content, list);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals(5, list.size());
        for (String line : list) {
            String[] array = StringUtils.split(line, '|');

            Assert.assertEquals(16, array.length);
            Assert.assertEquals("-------", array[0]);
            Assert.assertEquals("%", array[1]);
            Dates.parse(array[2]);
            LogLevel.of(array[3]);
            Assert.assertEquals(5, array[3].length());
            LogLevel.of(array[4]);
            Assert.assertEquals(5, array[4].length());
            Integer.parseInt(array[5]);
            Assert.assertTrue(array[6].length() > 0);
            Assert.assertEquals(array[9], array[7]);
            Assert.assertEquals(LogTest.class.getName(), array[8]);
            Integer.parseInt(array[10]);
            Assert.assertEquals(testValue, array[11]);
            Assert.assertTrue(array[12].startsWith("test level is "));
            Assert.assertEquals("log." + LogTest.class.getSimpleName(), array[13]);
            Assert.assertEquals(LogTest.class.getName(), array[14]);
            Assert.assertEquals(ClassUtils.getPackageName(ClassUtils.class, 2), array[15]);
        }
    }

    /**
     * 测试关闭输出功能
     */
    @Test
    public void test4() throws IOException {
        File logfile = FileUtils.createTempFile("LogTest_test4.log");

        LogContext context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());
        context.updateLevel("*", LogLevel.OFF);

        String pattern = "%d|%p|%level|%processId|%t|%l|%c|%C.%M(%F:%L)|mills=%r|%X{test}|%m%ex%n";
        Assert.assertNotNull(new FileAppender(logfile.getAbsolutePath(), CharsetName.UTF_8, pattern, false).setup(context));
        context.removeAppender(PatternConsoleAppender.class);

        Log log = LogFactory.getLog(context, LogTest.class);
        log.trace("test level is {}", "trace");
        log.debug("test level is {}", "debug");
        log.info("test level is {}", "info");
        log.error("test level is {}", "error");
        log.fatal("test level is {}", "fatal");
        context.findAppender(FileAppender.class).close();
        Assert.assertEquals("", FileUtils.readline(logfile, CharsetName.UTF_8, 0));
    }

    // 测试抛出异常
    @Test
    public void test5() throws IOException {
        File logfile = FileUtils.createTempFile("LogTest_test5.log");

        String pattern = "-------|%m%ex%n";
        System.setProperty(LogFactory.PROPERTY_LOG_SOUT, "");
        LogContextImpl context = new LogContextImpl();
        context.init();

        context.setBuilder(new PatternLogBuilder());
        context.findAppender(PatternConsoleAppender.class).pattern(pattern);
        FileAppender fileAppender = new FileAppender(logfile.getAbsolutePath(), CharsetName.UTF_8, pattern, true).charsetName(CharsetName.UTF_8);
        fileAppender.setup(context);
        context.removeAppender(PatternConsoleAppender.class);

        // 删除记录器
        context.removeAppender(fileAppender.getName());
        Assert.assertNull(context.findAppender(FileAppender.class));

        // 安装记录器
        Assert.assertNotNull(fileAppender.setup(context));
        context.removeAppender(fileAppender);
        fileAppender.setup(context);

        // 测试记录器名
        Assert.assertNotNull(fileAppender.getName());
        Assert.assertEquals(fileAppender.getName(), context.findAppender(FileAppender.class).getName());

        String str = "\r";
        str += "1\n";
        str += "12\r\n";
        str += "\n";
        str += "1345\r\n";
        str += "\n";

        LogFactory.getLog(context, LogTest.class).error(str, new NullPointerException());

        String content = FileUtils.readline(logfile, CharsetName.UTF_8, 0);
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(content, list);
        Assert.assertTrue(list.size() >= 8);
        Assert.assertEquals("java.lang.NullPointerException", list.get(6));
    }

    @Test
    public void test6() throws IOException {
        File logfile = FileUtils.createTempFile("LogTest_test6.log");

        String pattern = "-------|%m%ex%n";
        LogContext context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());
        context.findAppender(PatternConsoleAppender.class).pattern(pattern);
        Assert.assertNotNull(new FileAppender(logfile.getAbsolutePath(), CharsetName.UTF_8, pattern, true).setup(context));
        context.removeAppender(PatternConsoleAppender.class);

        LogFactory.getLog(context, LogTest.class).error(null);
        String content = FileUtils.readline(logfile, CharsetName.UTF_8, 0);
        Assert.assertEquals("-------|null" + Settings.LINE_SEPARATOR, content);
    }

    @Test
    public void test7() throws IOException {
        File logfile = FileUtils.createTempFile("LogTest_test7.log");

        String pattern = "-------|%m%ex%n";
        LogContext context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());
        context.findAppender(PatternConsoleAppender.class).pattern(pattern);
        Assert.assertNotNull(new FileAppender(logfile.getAbsolutePath(), CharsetName.UTF_8, pattern, true).setup(context));
        context.removeAppender(PatternConsoleAppender.class);

        LogFactory.getLog(context, LogTest.class).info("\n{}\n", 1);
        String content = FileUtils.readline(logfile, CharsetName.UTF_8, 0);
        Assert.assertEquals("-------|\n-------|1\n", content);
    }
}
