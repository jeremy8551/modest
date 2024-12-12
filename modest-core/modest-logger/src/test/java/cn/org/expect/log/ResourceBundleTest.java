package cn.org.expect.log;

import java.io.File;
import java.io.IOException;

import cn.org.expect.log.apd.DefaultLogTest;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class ResourceBundleTest {

    @Test
    public void test2() throws IOException {
        File tempFile = FileUtils.createTempFile("testLog.txt");
        System.out.println("file://" + tempFile.getAbsolutePath());
        LogFactory.set("sout+,>" + tempFile.getAbsolutePath());

        Log log = LogFactory.getLog(LogFactory.getContext(), DefaultLogTest.class, null, false);
        System.out.println(log.getClass().getName());

        log.info("a.b");
        log.info("a.b\ncde");
        log.info("a.b.c.d", "1", "2");
        log.info("a.b.c.d.e", "1\n2\n34\n5");

        String str = FileUtils.readline(tempFile, CharsetName.UTF_8, 0);
        Assert.assertEquals(str, "test ab" + FileUtils.lineSeparator + "a.b\ncde" + FileUtils.lineSeparator + "1 2" + FileUtils.lineSeparator + "1\n2\n34\n5\n");
    }
}
