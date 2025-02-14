package cn.org.expect.util;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.BufferLogger;
import cn.org.expect.log.JUL;
import org.junit.Assert;
import org.junit.Test;

public class JULTest {

    @Test
    public void test() {
        try {
            BufferLogger log = new BufferLogger();
            Logs.setLogger(log);

            Logs.trace("print trace");
            Logs.trace("print trace", new NullPointerException());

            Logs.debug("print debug");
            Logs.debug("print debug", new NullPointerException());

            Logs.info("print info");
            Logs.info("print info", new NullPointerException());

            Logs.warn("print warn");
            Logs.warn("print warn", new NullPointerException());

            Logs.error("print error");
            Logs.error("print error", new NullPointerException());

            Logs.fatal("print fatal");
            Logs.fatal("print fatal", new NullPointerException());

            String str = log.toString();
            List<String> list = StringUtils.splitLines(str, new ArrayList<String>());

            List<String> newList = new ArrayList<String>();
            for (String line : list) {
                if (line.startsWith("print")) {
                    newList.add(line);
                }
            }

            Assert.assertNotEquals(-1, str.indexOf("NullPointerException"));

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
        } finally {
            Logs.setLogger(JUL.out);
        }
    }

    @Test
    public void test1() {
        StackTraceElement e = StackTraceUtils.get("1245");
        Assert.assertNotNull(e);
        Assert.assertEquals("?", e.getClassName());
    }
}
