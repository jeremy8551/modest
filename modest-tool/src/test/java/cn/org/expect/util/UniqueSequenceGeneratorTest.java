package cn.org.expect.util;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;

public class UniqueSequenceGeneratorTest {

    final static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();

    final static HashSet<String> error = new HashSet<String>();

    final static UniqueSequenceGenerator SEQUENCE = new UniqueSequenceGenerator("ID-{}", 1000);

    @Test
    public void test() throws InterruptedException {

        // 创建多个线程
        RunnableImpl task1 = new RunnableImpl();
        RunnableImpl task2 = new RunnableImpl();
        RunnableImpl task3 = new RunnableImpl();

        Thread t1 = new Thread(task1, "Thread-1");
        Thread t2 = new Thread(task2, "Thread-2");
        Thread t3 = new Thread(task3, "Thread-3");

        // 启动线程
        t1.start();
        t2.start();
        t3.start();

        // 等待
        Dates.waitFor(new WaitTasks(task1, task2, task3), 500, 10 * 1000);

        if (!error.isEmpty()) {
            Assert.fail();
        }
    }

    @Test
    public void test1() {
        UniqueSequenceGenerator sequence = new UniqueSequenceGenerator("ID-{}", 1000);
        Assert.assertEquals("ID-1000", sequence.nextString());
    }

    static class WaitTasks implements Dates.Condition {

        RunnableImpl task1;

        RunnableImpl task2;

        RunnableImpl task3;

        public WaitTasks(RunnableImpl task1, RunnableImpl task2, RunnableImpl task3) {
            this.task1 = task1;
            this.task2 = task2;
            this.task3 = task3;
        }

        public boolean test() {
            return !task1.isFinish() || !task2.isFinish() || !task3.isFinish();
        }
    }

    static class RunnableImpl implements Runnable {

        volatile boolean finish;

        public RunnableImpl() {
            finish = false;
        }

        public void run() {
            for (int i = 0; i < 1000; i++) {
                String value = SEQUENCE.nextString();
                if (map.contains(value)) {
                    error.add("");
                } else {
                    map.put(value, value);
                }
            }
            finish = true;
        }

        public boolean isFinish() {
            return finish;
        }
    }
}
