package cn.org.expect.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TimeWatchTest {

    @Before
    public void setUp() {
        File dir = FileUtils.getTempDir("test", TimeWatchTest.class.getSimpleName());
        FileUtils.assertClearDirectory(dir);
    }

    @Test
    public void test() {
        TimeWatch watch = new TimeWatch();

        // 测试暂停
        Assert.assertEquals(watch.useSeconds(), 0);
        watch.pauseOrKeep();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logs.error(e.getLocalizedMessage(), e);
        }
        Assert.assertEquals(watch.useSeconds(), 0);

        // 测试暂停后启动
        watch.pauseOrKeep();
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Logs.error(e.getLocalizedMessage(), e);
        }
        Assert.assertEquals(watch.useSeconds(), 2);

        // 测试用时
        watch.start();
        Assert.assertEquals(watch.useSeconds(), 0);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logs.error(e.getLocalizedMessage(), e);
        }
        Assert.assertEquals(watch.useSeconds(), 1);
    }
}
