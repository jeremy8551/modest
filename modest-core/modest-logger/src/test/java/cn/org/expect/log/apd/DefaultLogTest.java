package cn.org.expect.log.apd;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogLevel;
import cn.org.expect.log.PatternLogBuilder;
import org.junit.Test;

public class DefaultLogTest {

    @Test
    public void test1() {
        LogContext context = LogFactory.getContext();
        context.updateLevel("", LogLevel.TRACE);
        context.setBuilder(new PatternLogBuilder());

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
    }
}
