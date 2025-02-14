package cn.org.expect.collection;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class NatureRandomListTest {

    @Test
    public void test() {
        RandomAccessList<String> list = new RandomAccessList<String>();
        Assert.assertEquals(0, list.size());

        list.add("1");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("1", list.get(0));

        list.add("2");
        Assert.assertEquals("2", list.get(1));

        list.add("3");
        list.add("测试");

        Assert.assertEquals(4, list.size());
        RandomAccessList<String> clone = (RandomAccessList<String>) list.clone();
        Assert.assertEquals("RandomAccessList[1, 2, 3, 测试]", StringUtils.toString(clone));

        list.add(0, "0");
        Assert.assertEquals("0", list.get(0));
        Assert.assertEquals("1", list.get(1));

        list.clear();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(0, list.size());

        list.addAll(clone);
        Assert.assertEquals("RandomAccessList[1, 2, 3, 测试]", StringUtils.toString(list));

        list.addAll(0, clone);
        Assert.assertEquals("RandomAccessList[1, 2, 3, 测试, 1, 2, 3, 测试]", StringUtils.toString(list));

        list.clear();
        list.addAll(clone);
        list.addAll(4, clone);
        Assert.assertEquals("RandomAccessList[1, 2, 3, 测试, 1, 2, 3, 测试]", StringUtils.toString(list));

        list.clear();
        list.addAll(clone);
        list.addAll(2, clone);
        Assert.assertEquals("RandomAccessList[1, 2, 1, 2, 3, 测试, 3, 测试]", StringUtils.toString(list));

        list.clear();
        list.addAll(clone);
        Assert.assertEquals(list, clone);

        Assert.assertEquals(0, list.indexOf("1"));
        Assert.assertEquals(3, list.indexOf("测试"));

        list.clear();
        list.addAll(clone);
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String str = it.next();
            if (str.equals("测试")) {
                it.remove();
            }
            if (str.equals("2")) {
                it.remove();
            }
        }
        Assert.assertEquals("RandomAccessList[1, 3]", StringUtils.toString(list));

        list.clear();
        list.addAll(clone);
        it = list.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        Assert.assertTrue(list.isEmpty());

        list.clear();
        list.addAll(clone);
        Assert.assertEquals(0, list.lastIndexOf("1"));
        Assert.assertEquals(3, list.lastIndexOf("测试"));

        list.clear();
        list.addAll(clone);
        list.remove("1");
        Assert.assertEquals("RandomAccessList[2, 3, 测试]", StringUtils.toString(list));
        list.remove("测试");
        Assert.assertEquals("RandomAccessList[2, 3]", StringUtils.toString(list));

        list.clear();
        list.addAll(clone);
        list.remove(0);
        Assert.assertEquals("RandomAccessList[2, 3, 测试]", StringUtils.toString(list));
        list.remove(2);
        Assert.assertEquals("RandomAccessList[2, 3]", StringUtils.toString(list));

        list.clear();
        list.addAll(clone);
        list.set(0, "4");
        Assert.assertEquals("RandomAccessList[4, 2, 3, 测试]", StringUtils.toString(list));
        list.set(3, "4");
        Assert.assertEquals("RandomAccessList[4, 2, 3, 4]", StringUtils.toString(list));

        list.clear();
        list.addAll(clone);
        List<String> subList = list.subList(0, 1);
        Assert.assertEquals("RandomAccessSubList[1]", StringUtils.toString(subList));
        subList = list.subList(0, 0);
        Assert.assertEquals("RandomAccessSubList[]", StringUtils.toString(subList));
        subList = list.subList(0, 4);
        Assert.assertEquals("RandomAccessSubList[1, 2, 3, 测试]", StringUtils.toString(subList));

        String[] array = new String[list.size()];
        list.toArray(array);
        Assert.assertEquals("String[1, 2, 3, 测试]", StringUtils.toString(array));

        Object[] array1 = list.toArray();
        Assert.assertEquals("Object[1, 2, 3, 测试]", StringUtils.toString(array1));

        list.clear();
        list.addAll(clone);
        list.removeAll(clone);
        Assert.assertTrue(list.isEmpty());

        list.clear();
        list.addAll(clone);
        list.retainAll(clone);
        Assert.assertEquals("RandomAccessList[1, 2, 3, 测试]", StringUtils.toString(list));

        list.clear();
        list.addAll(clone);
        // clone.remove("测试");
        // list.retainAll(clone);
        // assertTrue(ST.toString(list, " ").equals("RandomAccessList[1, 2, 3]"));
        Assert.assertTrue(list.containsAll(clone));

        list.expandCapacity(10);
        Assert.assertEquals(4, list.size());

        StringBuilder buf = new StringBuilder();
        list.clear();
        list.addAll(clone);
        ListIterator<String> lt = list.listIterator();
        while (lt.hasNext()) {
            buf.append(lt.next());
        }
        Assert.assertEquals("123", buf.toString());

        buf.setLength(0);
        list.clear();
        list.addAll(clone);
        lt = list.listIterator(1);
        while (lt.hasNext()) {
            buf.append(lt.next());
        }
        Assert.assertEquals("23", buf.toString());

        list.clear();
        list.addAll(clone);
//		assertTrue(list.toString());
    }
}
