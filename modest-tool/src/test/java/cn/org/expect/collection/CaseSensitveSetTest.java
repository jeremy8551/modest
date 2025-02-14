package cn.org.expect.collection;

import java.util.HashSet;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class CaseSensitveSetTest {

    @Test
    public void test() {
        CaseSensitivSet set = new CaseSensitivSet();
        Assert.assertTrue(set.isEmpty());

        set.add(null);
        set.add("");
        set.add("a");
        set.add("A");
        Assert.assertEquals(set.size(), 3);

        set.clear();
        set.add(null);
        set.add("");
        set.add("a");
        set.add("A");
        set.add("Abc");
        set.add("abc");
        Assert.assertEquals(set.size(), 4);

        Iterator<String> it = set.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertNull(it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), "");
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), "a");
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), "Abc");
        Assert.assertFalse(it.hasNext());

        Object[] array = set.toArray();
        Assert.assertNull(array[0]);
        Assert.assertEquals(array[1], "");
        Assert.assertEquals(array[2], "a");
        Assert.assertEquals(array[3], "Abc");

        array = set.toArray(new String[4]);
        Assert.assertNull(array[0]);
        Assert.assertEquals(array[1], "");
        Assert.assertEquals(array[2], "a");
        Assert.assertEquals(array[3], "Abc");

        // 测试删除全部数据
        for (it = set.iterator(); it.hasNext(); ) {
            it.next();
            it.remove();
        }
        Assert.assertTrue(set.isEmpty());

        // 测试删除全部数据
        set.add(null);
        set.add("");
        set.add("a");
        set.add("A");
        set.add("Abc");
        set.add("abc");
        Assert.assertTrue(set.contains(null));
        Assert.assertTrue(set.contains(""));
        Assert.assertTrue(set.contains("A"));
        Assert.assertTrue(set.contains("aBc"));
        HashSet<String> test = new HashSet<String>();
        test.add("A");
        test.add("aBc");
        test.add("");
        test.add(null);
        Assert.assertTrue(set.containsAll(test));
        Assert.assertTrue(set.removeAll(test) && set.isEmpty());

        // 测试添加集合元素
        set.clear();
        set.add(null);
        set.add("");
        set.add("a");
        set.add("A");
        set.add("Abc");
        set.add("abc");
        int old = set.size();
        Assert.assertTrue(!set.addAll(test) && old == set.size());

        set.clear();
        set.add(null);
        set.add("");
        set.add("a");
        set.add("A");
        set.add("Abc");
        set.add("abc");

        // 测试顺序
        it = set.iterator();
        Assert.assertNull(it.next());
        Assert.assertEquals(it.next(), "");
        Assert.assertEquals(it.next(), "a");
        Assert.assertEquals(it.next(), "Abc");

        old = set.size();
        Assert.assertTrue(!set.retainAll(test) && old == set.size());
    }
}
