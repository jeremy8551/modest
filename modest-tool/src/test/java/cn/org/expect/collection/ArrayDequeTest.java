package cn.org.expect.collection;

import java.util.PriorityQueue;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;

public class ArrayDequeTest {

    @Test
    public void test() {
        ArrayDeque<String> queue = new ArrayDeque<String>();
        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("1", queue.poll());
        Assert.assertEquals(2, queue.size());

        Assert.assertEquals("2", queue.poll());
        Assert.assertEquals(1, queue.size());

        Assert.assertEquals("3", queue.poll());
        Assert.assertEquals(0, queue.size());

        Assert.assertNull(queue.poll());
        Assert.assertEquals(0, queue.size());

        Assert.assertNull(queue.poll());
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void test1() {
        Queue<Integer> priorityQueue = new PriorityQueue<Integer>();
        priorityQueue.add(1);
        priorityQueue.add(4);
        priorityQueue.add(2);
        priorityQueue.add(3);

        Assert.assertEquals(new Integer(1), priorityQueue.poll());
        Assert.assertEquals(new Integer(2), priorityQueue.poll());
        Assert.assertEquals(new Integer(3), priorityQueue.poll());
        Assert.assertEquals(new Integer(4), priorityQueue.poll());
    }
}
