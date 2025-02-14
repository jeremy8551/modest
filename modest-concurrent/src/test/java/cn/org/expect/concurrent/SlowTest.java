package cn.org.expect.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.org.expect.concurrent.internal.DefaultJobReader;
import cn.org.expect.concurrent.internal.DefaultJobWriter;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogLevel;
import cn.org.expect.util.Dates;
import cn.org.expect.util.IO;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试慢速并发任务
 */
public class SlowTest {
    private final static Log log = LogFactory.getLog(SlowTest.class);

    @Test
    public void test1() throws Exception {
        LogFactory.getContext().updateLevel("*", LogLevel.DEBUG);
        EasyThreadSource source = new EasyThreadSource();
        EasyJobService service = source.getJobService(2);

        List<EasyJob> list = new ArrayList<EasyJob>();
        for (int i = 0; i < 10; i++) {
            list.add(new Task(i + 1));
        }

        service.execute(new DefaultJobReader(list));
        IO.close(source);

        Assert.assertEquals(0, service.getAliveJob());
        Assert.assertEquals(0, service.getErrorJob());
        Assert.assertEquals(10, service.getStartJob());
    }

    @Test
    public void test2() throws Exception {
        LogFactory.getContext().updateLevel("*", LogLevel.DEBUG);
        ThreadSource source = new EasyThreadSource();
        EasyJobService service = source.getJobService(2);

        List<EasyJob> list = new ArrayList<EasyJob>();
        for (int i = 0; i < 10; i++) {
            if (i == 3) {
                list.add(new TaskThrowable(i + 1));
            } else {
                list.add(new Task(i + 1));
            }
        }

        DefaultJobWriter out = new DefaultJobWriter();
        service.execute(new DefaultJobReader(list), out);
        source.close();

        Assert.assertEquals(0, service.getAliveJob());
        Assert.assertEquals(1, service.getErrorJob());
        Assert.assertEquals(10, service.getStartJob());
        Assert.assertEquals(1, out.getMessages().size());
        log.info(out.getMessages().get(0), out.getThrowables().get(0));
    }

    private static class TaskThrowable extends Task {

        public TaskThrowable(int n) {
            super(n);
        }

        public int execute() throws Exception {
            Random random = new Random();

            int v = random.nextInt(10);
            if (v == 0) {
                v = 1;
            }

            log.info(this.getName() + " " + " sleep " + v + " second!");
            Dates.sleep(v * 1000);

            throw new RuntimeException("测试抛出异常");
        }
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
            Random random = new Random();

            int v = random.nextInt(10);
            if (v == 0) {
                v = 1;
            }

            log.info(this.getName() + " " + " sleep " + v + " second!");
            Dates.sleep(v * 1000);

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
