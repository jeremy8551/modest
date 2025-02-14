package cn.org.expect.collection;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class CaseSensitivMapTest {

    @Test
    public void test() {
        CaseSensitivMap<String> map = new CaseSensitivMap();
        Assert.assertTrue(map.isEmpty());

        map.put("1", "");
        map.put("1", "2");
        map.put("a", "");
        map.put("A", "2");
        map.put("aB", "");
        map.put("Ab", "2");
        map.put("abc", "");
        Assert.assertEquals(map.size(), 4);

        Assert.assertEquals(map.get("1"), "2");
        Assert.assertEquals(map.get("a"), "2");
        Assert.assertEquals(map.get("Ab"), "2");
        Assert.assertEquals(map.get("ABC"), "");

        Assert.assertTrue(!map.containsKey(""));
        Assert.assertTrue(map.containsKey("1"));
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("A"));
        Assert.assertTrue(map.containsKey("Ab"));
        Assert.assertTrue(map.containsKey("aB"));
        Assert.assertTrue(map.containsKey("abc"));
        Assert.assertTrue(map.containsValue(""));
        Assert.assertTrue(map.containsValue("2"));
        Assert.assertTrue(!map.containsValue("3"));

        map.clear();
        Assert.assertTrue(map.isEmpty());

        map.put("1", "");
        map.put("1", "2");
        map.put("a", "");
        map.put("A", "2");
        map.put("aB", "");
        map.put("Ab", "2");
        map.put("abc", "");

        Collection<String> c = map.values();
        Assert.assertTrue(c.contains("2"));
        Assert.assertTrue(c.contains(""));
        Assert.assertTrue(!c.contains("1"));

        Set<String> keys = map.keySet();
        Assert.assertTrue(keys.contains("1"));
        Assert.assertTrue(keys.contains("a"));
        Assert.assertTrue(keys.contains("ab"));
        Assert.assertTrue(keys.contains("abc"));
        Assert.assertTrue(!keys.contains(""));

        Set<String> set = new CaseSensitivSet();
        Set<Entry<String, String>> entrySet = map.entrySet();
        for (Entry<String, String> e : entrySet) {
            Assert.assertTrue(!set.contains(e.getKey()));
            set.add(e.getKey());
        }

        map.remove("1");
        Assert.assertEquals(map.size(), 3);
        map.remove("a");
        Assert.assertEquals(map.size(), 2);
        map.remove("AB");
        Assert.assertEquals(map.size(), 1);
        map.remove("ABc");
        Assert.assertEquals(map.size(), 0);
    }
}
