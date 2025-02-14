package cn.org.expect.log.slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogLevel;
import cn.org.expect.log.apd.DefaultLogTest;
import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class Slf4JLogTest {

    @Test
    public void test1() throws IOException {
        Assert.assertTrue(Slf4jLogBuilder.support());
        LogContext context = new LogContextImpl();
        context.updateLevel("", LogLevel.TRACE);
        context.setBuilder(new Slf4jLogBuilder());

        Log log = LogFactory.getLog(context, DefaultLogTest.class, null, false);
        log.trace("print trace");
        log.trace("print\n trace");
        log.trace("print trace", new NullPointerException());

        log.debug("print debug");
        log.debug("print\n debug");
        log.debug("print debug", new NullPointerException());

        log.info("print info");
        log.info("print\n info");
        log.info("print info", new NullPointerException());

        log.warn("print warn");
        log.warn("print\n warn");
        log.warn("print warn", new NullPointerException());

        log.error("print error");
        log.error("print\n error");
        log.error("print error", new NullPointerException());

        log.fatal("print fatal");
        log.fatal("print\n fatal");
        log.fatal("print fatal", new NullPointerException());

        File logfile = new File(FileUtils.getTempDir("slf4j"), "test.log");
        String text = FileUtils.readline(logfile, CharsetName.UTF_8, 0);
        FileUtils.delete(logfile);

        List<String[]> newList = new ArrayList<String[]>();
        List<String> list = StringUtils.splitLines(text, new ArrayList<String>());
        for (String line : list) {
            String[] array = StringUtils.split(line, '|');
            if (array.length == 7) {
                newList.add(array);
            }
        }

        Assert.assertEquals("INFO ", newList.get(0)[2]);
        Assert.assertEquals("print info", newList.get(0)[6]);
        Assert.assertEquals("INFO ", newList.get(1)[2]);
        Assert.assertEquals("print", newList.get(1)[6]);
        Assert.assertEquals("INFO ", newList.get(2)[2]);
        Assert.assertEquals(" info", newList.get(2)[6]);
        Assert.assertEquals("INFO ", newList.get(3)[2]);
        Assert.assertEquals("print info", newList.get(3)[6]);

        Assert.assertEquals("WARN ", newList.get(4)[2]);
        Assert.assertEquals("print warn", newList.get(4)[6]);
        Assert.assertEquals("WARN ", newList.get(5)[2]);
        Assert.assertEquals("print", newList.get(5)[6]);
        Assert.assertEquals("WARN ", newList.get(6)[2]);
        Assert.assertEquals(" warn", newList.get(6)[6]);
        Assert.assertEquals("WARN ", newList.get(7)[2]);
        Assert.assertEquals("print warn", newList.get(7)[6]);

        Assert.assertEquals("ERROR", newList.get(8)[2]);
        Assert.assertEquals("print error", newList.get(8)[6]);
        Assert.assertEquals("ERROR", newList.get(9)[2]);
        Assert.assertEquals("print", newList.get(9)[6]);
        Assert.assertEquals("ERROR", newList.get(10)[2]);
        Assert.assertEquals(" error", newList.get(10)[6]);
        Assert.assertEquals("ERROR", newList.get(11)[2]);
        Assert.assertEquals("print error", newList.get(11)[6]);

        Assert.assertEquals("ERROR", newList.get(12)[2]);
        Assert.assertEquals("print fatal", newList.get(12)[6]);
        Assert.assertEquals("ERROR", newList.get(13)[2]);
        Assert.assertEquals("print", newList.get(13)[6]);
        Assert.assertEquals("ERROR", newList.get(14)[2]);
        Assert.assertEquals(" fatal", newList.get(14)[6]);
        Assert.assertEquals("ERROR", newList.get(15)[2]);
        Assert.assertEquals("print fatal", newList.get(15)[6]);
    }

    @Test
    public void test2() {
        LogContext context = new LogContextImpl();
        context.updateLevel("", LogLevel.TRACE);
        context.setBuilder(new Slf4jLogBuilder());

        Log log = LogFactory.getLog(context, DefaultLogTest.class, null, false);
        Assert.assertFalse(log.isTraceEnabled());
        Assert.assertFalse(log.isDebugEnabled());
        Assert.assertTrue(log.isInfoEnabled());
        Assert.assertTrue(log.isWarnEnabled());
        Assert.assertTrue(log.isErrorEnabled());
        Assert.assertTrue(log.isFatalEnabled());

        Assert.assertEquals(DefaultLogTest.class.getName(), log.getName());
    }
}
