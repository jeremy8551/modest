package cn.org.expect.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class CollectionUtilsTest {

    @Test
    public void testIsEmptyMapOfEF() {
        Map<String, String> map = null;
        Assert.assertTrue(CollectionUtils.isEmpty(map));

        map = new HashMap<String, String>();
        Assert.assertTrue(CollectionUtils.isEmpty(map));

        map.put("", "");
        Assert.assertTrue(!CollectionUtils.isEmpty(map));
    }

    @Test
    public void testIsEmptyCollectionOfE() {
        List<String> map = null;
        Assert.assertTrue(CollectionUtils.isEmpty(map));

        map = new ArrayList<String>();
        Assert.assertTrue(CollectionUtils.isEmpty(map));

        map.add("");
        Assert.assertTrue(!CollectionUtils.isEmpty(map));
    }

    @Test
    public void testRemoveDuplicatData() {
        List<String> list = new ArrayList<String>();
        list.add("");
        list.add("");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("2");
        list.add("2");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("9");

        List<String> newlist = CollectionUtils.removeDuplicates(list, null);
        Assert.assertEquals(newlist.size(), 8);

        newlist = CollectionUtils.removeDuplicates(list, new StringComparator());
        Assert.assertEquals(newlist.size(), 8);
    }

    @Test
    public void testToHashMap() {
        String[] keys = {"key1", "key2"};
        String[] vals = {"val1", "val2"};
        Map<String, String> map = CollectionUtils.toHashMap(keys, vals);
        Assert.assertEquals(map.get("key1"), "val1");
        Assert.assertEquals(map.get("key2"), "val2");
    }

    @Test
    public void testToArray() {
        List<String> list = new ArrayList<String>();
        list.add("");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");

        Assert.assertTrue(ArrayUtils.equals(CollectionUtils.toArray(list), new String[]{"", "1", "2", "3", "4", "5", "6"}, null));
    }

    @Test
    public void testToList() {
    }

    @Test
    public void testOnlyOne() {
        List<String> list = new ArrayList<String>();
        try {
            CollectionUtils.onlyOne(list);
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        list.add("");
        Assert.assertEquals(CollectionUtils.onlyOne(list), "");

        list.add("1");
        try {
            CollectionUtils.onlyOne(list);
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testFirst() {
        List<String> list = null;
        Assert.assertEquals(CollectionUtils.first(list), null);

        list = new ArrayList<String>();
        Assert.assertEquals(CollectionUtils.first(list), null);

        list.add("1");
        Assert.assertEquals(CollectionUtils.first(list), "1");

        list.add("1");
        list.add("2");
        Assert.assertEquals(CollectionUtils.first(list), "1");
    }

    @Test
    public void testLast() {
        List<String> list = null;
        Assert.assertEquals(CollectionUtils.last(list), null);

        list = new ArrayList<String>();
        Assert.assertEquals(CollectionUtils.last(list), null);

        list.add("1");
        Assert.assertEquals(CollectionUtils.last(list), "1");

        list.add("1");
        list.add("2");
        Assert.assertEquals(CollectionUtils.last(list), "2");
    }

    @Test
    public void testGetDiffAttrVal() {
        HashMap<String, String> m1 = new HashMap<String, String>();
        m1.put("a", "a");
        m1.put("b", "b");
        m1.put("c", "c");
        m1.put("d", "d");
        m1.put("e", "e");

        HashMap<String, String> m2 = new HashMap<String, String>();
        m2.put("a", "a1");
        m2.put("c", "c");
        m2.put("d", "d1");
        m2.put("e", "e");

        Assert.assertEquals(CollectionUtils.getDiffAttrVal(m1, m2, null), ArrayUtils.asList("a", "d"));
        Assert.assertEquals(CollectionUtils.getDiffAttrVal(m1, m2, new StringComparator()), ArrayUtils.asList("a", "d"));
    }

    @Test
    public void testElementAt() {
        List<String> list = new ArrayList<String>();
        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");

        Assert.assertNull(CollectionUtils.elementAt(list, -1));
        Assert.assertNull(CollectionUtils.elementAt(list, list.size()));
        Assert.assertNull(CollectionUtils.elementAt(list, list.size() + 1));
        Assert.assertEquals(CollectionUtils.elementAt(list, 0), "0");
        Assert.assertEquals(CollectionUtils.elementAt(list, 1), "1");
    }

    @Test
    public void testCloneProperties() {
        Properties p = new Properties();
        p.setProperty("name", "value1");
        p.put(new Integer(1), "value2");

        Properties np = new Properties();
        CollectionUtils.cloneProperties(p, np);
        Assert.assertEquals(np.get("name"), "value1");
        Assert.assertEquals(np.get(new Integer(1)), "value2");
    }

    @Test
    public void testContains() {
        List<String> list = new ArrayList<String>();
        list.add(null);
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");

        Assert.assertTrue(CollectionUtils.contains(list, null, new StringComparator()));
        Assert.assertTrue(CollectionUtils.contains(list, "6", new StringComparator()));
    }

    @Test
    public void testContainsKeyIgnoreCase() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("t", "");
        map.put("a", "");
        map.put("d", "");
        map.put("c", "");

        Assert.assertTrue(CollectionUtils.containsKeyIgnoreCase(map, "T"));
        Assert.assertTrue(CollectionUtils.containsKeyIgnoreCase(map, "C"));
        Assert.assertTrue(CollectionUtils.containsKeyIgnoreCase(map, "d"));
    }
}
