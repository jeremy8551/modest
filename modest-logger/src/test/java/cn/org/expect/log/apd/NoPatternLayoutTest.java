package cn.org.expect.log.apd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.org.expect.log.Appender;
import cn.org.expect.log.ExecutorImpl;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogSettings;
import cn.org.expect.log.file.FileAppender;
import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.PatternConsoleAppender;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class NoPatternLayoutTest {

    @Test
    public void test() throws IOException {
        File file = FileUtils.createTempFile("NoPatternLayoutTest0.log");

        LogContext context = new LogContextImpl();
        Assert.assertEquals(0, new LogSettings(context).load("info:sout").length);

        PatternConsoleAppender appender = context.findAppender(PatternConsoleAppender.class);
        Assert.assertNotNull(appender);

        String pattern = appender.getPattern();
        Appender fileAppender = new FileAppender(file.getAbsolutePath(), CharsetName.UTF_8, pattern, false).setup(context);
        context.removeAppender(PatternConsoleAppender.class);

        Log log = LogFactory.getLog(context, NoPatternLayoutTest.class);
        log.info("test", new NullPointerException());
        fileAppender.close();

        String content = FileUtils.readline(file, CharsetName.UTF_8, 0);
        ArrayList<String> list = new ArrayList<String>();
        StringUtils.splitLines(content, list);
        Assert.assertTrue("日志内容[" + content + "]", list.size() >= 2);
        Assert.assertEquals("test", list.get(0));
        Assert.assertEquals("java.lang.NullPointerException", list.get(1).toString().trim());
    }

    @Test
    public void test1() throws IOException {
        File file = FileUtils.createTempFile("NoPatternLayoutTest1.log");

        LogContext context = new LogContextImpl();
        Assert.assertEquals(0, new LogSettings(context).load("info:sout").length);

        PatternConsoleAppender appender = context.findAppender(PatternConsoleAppender.class);
        Assert.assertNotNull(appender);

        String pattern = appender.getPattern();
        Appender appender1 = new FileAppender(new ExecutorImpl(), file.getAbsolutePath(), CharsetName.UTF_8, pattern, 5000, true).setup(context);
        context.removeAppender(PatternConsoleAppender.class);

        Log log = LogFactory.getLog(context, NoPatternLayoutTest.class);
        log.info("test", new NullPointerException());
        appender1.close();

        String content = FileUtils.readline(file, CharsetName.UTF_8, 0);
        ArrayList<String> list = new ArrayList<String>();
        StringUtils.splitLines(content, list);
        Assert.assertTrue(content, list.size() >= 2);
        Assert.assertEquals("test", list.get(0));
        Assert.assertEquals("java.lang.NullPointerException", list.get(1).toString().trim());
    }
}
