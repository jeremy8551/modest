package cn.org.expect.concurrent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.concurrent.internal.DefaultJobReader;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试快速并发任务
 */
public class QuickTest {
    private final static Log log = LogFactory.getLog(QuickTest.class);

    /**
     * 因为涉及到多线程计算，所以重复测试30次，防止出现并发问题
     */
    @Test
    public void test() throws Exception {
        File logfile = FileUtils.createTempFile(QuickTest.class.getSimpleName() + ".log");
        System.setProperty("QuickTestLogfile", logfile.getAbsolutePath());
        log.info("将日志输出到 file://{}", logfile);
        LogFactory.load(">>${QuickTestLogfile}+,sout-,debug");
        for (int i = 0; i < 30; i++) {
            this.run();
            log.info("");
            log.info("");
            log.info("");
        }
    }

    public void run() throws Exception {
        EasyThreadSource source = new EasyThreadSource();
        EasyJobService service = source.getJobService(5);

        int size = 70;
        List<EasyJob> list = new ArrayList<EasyJob>();
        for (int i = 0; i < size; i++) {
            list.add(new Task(i + 1));
        }

        service.execute(new DefaultJobReader(list));
        IO.close(source);

        Assert.assertEquals(0, service.getAliveJob());
        Assert.assertEquals(0, service.getErrorJob());
        Assert.assertEquals(size, service.getStartJob());
    }

    private static class Task implements EasyJob {
        protected int n;

        public Task(int n) {
            this.n = n;
        }

        public String getName() {
            return "JOB" + this.n;
        }

        public int execute() throws Exception {

            for (int i = 0; i < 2000000; i++) {
                i += 3;
            }

            log.info(this.getName() + " " + " over!");
            return 0;
        }

        public boolean isTerminate() {
            return false;
        }

        public void terminate() {
        }
    }
}
