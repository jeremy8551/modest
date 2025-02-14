package cn.org.expect.log.apd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.Appender;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogLevel;
import cn.org.expect.log.internal.LogBuilderAppender;
import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.PatternConsoleAppender;
import cn.org.expect.log.internal.PatternLogBuilder;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class DefaultLogTest {

    @Test
    public void test1() throws IOException {
        LogContext context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());
        context.updateLevel("", LogLevel.TRACE);
        context.removeAppender(PatternConsoleAppender.class);
        Appender appender = new LogBuilderAppender(LogFactory.SOUT_PLUS_PATTERN).setup(context);

        Log log = LogFactory.getLog(context, DefaultLogTest.class, null, false);
        log.trace("print trace");
        log.trace("print trace", new NullPointerException());

        log.debug("print debug");
        log.debug("print debug", new NullPointerException());

        log.info("print info");
        log.info("print info", new NullPointerException());

        log.warn("print warn");
        log.warn("print warn", new NullPointerException());

        log.error("print error");
        log.error("print error", new NullPointerException());

        log.fatal("print fatal");
        log.fatal("print fatal", new NullPointerException());

        List<String> newList = new ArrayList<String>();
        String text = appender.toString();
        List<String> list = StringUtils.splitLines(text, new ArrayList<String>());
        for (String line : list) {
            String[] array = StringUtils.split(line, '|');
            if (array.length == 5) {
                newList.add(ArrayUtils.last(array));
            }
        }

        Assert.assertEquals("print trace", newList.get(0));
        Assert.assertEquals("print trace", newList.get(1));
        Assert.assertEquals("print debug", newList.get(2));
        Assert.assertEquals("print debug", newList.get(3));
        Assert.assertEquals("print info", newList.get(4));
        Assert.assertEquals("print info", newList.get(5));
        Assert.assertEquals("print warn", newList.get(6));
        Assert.assertEquals("print warn", newList.get(7));
        Assert.assertEquals("print error", newList.get(8));
        Assert.assertEquals("print error", newList.get(9));
        Assert.assertEquals("print fatal", newList.get(10));
        Assert.assertEquals("print fatal", newList.get(11));
    }
}
