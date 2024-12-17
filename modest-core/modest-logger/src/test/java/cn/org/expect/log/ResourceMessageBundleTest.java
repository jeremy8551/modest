package cn.org.expect.log;

import java.io.File;

import cn.org.expect.log.apd.DefaultLogTest;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourceMessageBundleMap;
import org.junit.Assert;
import org.junit.Test;

public class ResourceMessageBundleTest {

    @Test
    public void test() throws Exception {
        File tempFile = FileUtils.createTempFile("testLog.txt");
        System.out.println("file://" + tempFile.getAbsolutePath());
        LogFactory.load("sout+,>" + tempFile.getAbsolutePath());

        LogContext context = LogFactory.getContext();
        context.setResourceBundle(new ResourceMessageBundleMap(null));
        Log log = LogFactory.getLog(context, DefaultLogTest.class, null, false);
        System.out.println(log.getClass().getName());

        log.info("a.b");
        log.info("a.b\ncde");
        log.info("a.b.c.d", "1", "2");
        log.info("a.b.c.d.e", "1\n2\n34\n5");
        log.info("test.no.key", "noKey");

        String str = FileUtils.readline(tempFile, CharsetName.UTF_8, 0);
        Assert.assertEquals(str, "test ab" + FileUtils.lineSeparator + "a.b\ncde" + FileUtils.lineSeparator + "1 2" + FileUtils.lineSeparator + "1\n2\n34\n5\ntest.no.key\n");
    }
}
