package cn.org.expect.util;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilsTest {

    @Test
    public void testindexOf() {
        Comparator<String> c = new Comparator<String>() {
            public int compare(String o1, String o2) {
                return 0;
            }
        };

        StringBuilder buf = new StringBuilder();
        Object[] array = new Object[]{0, 12.12, "test", buf, c};
        Assert.assertEquals(ArrayUtils.indexOf(array, Integer.class, 0), new Integer(0));
        Assert.assertEquals(ArrayUtils.indexOf(array, String.class, 0), "test");
        Assert.assertEquals(ArrayUtils.indexOf(array, Comparator.class, 0), c);
        Assert.assertEquals(ArrayUtils.indexOf(array, StringBuilder.class, 0), buf);
    }

    @Test
    public void testRemoveDuplicatesDataListOfTComparatorOfT() {
        Assert.assertTrue(Arrays.equals(new int[]{}, ArrayUtils.removeDuplicates(new int[]{})));
        Assert.assertTrue(Arrays.equals(new int[]{0}, ArrayUtils.removeDuplicates(new int[]{0})));
        Assert.assertTrue(Arrays.equals(new int[]{0}, ArrayUtils.removeDuplicates(new int[]{0, 0, 0})));
        Assert.assertTrue(Arrays.equals(new int[]{0, 1, 2}, ArrayUtils.removeDuplicates(new int[]{0, 1, 2})));
        Assert.assertTrue(Arrays.equals(new int[]{0, 1, 2}, ArrayUtils.removeDuplicates(new int[]{0, 1, 2, 1, 0, 2})));

        Assert.assertFalse(Arrays.equals(new int[]{0}, ArrayUtils.removeDuplicates(new int[]{1})));
        Assert.assertFalse(Arrays.equals(new int[]{0, 1, 2}, ArrayUtils.removeDuplicates(new int[]{0, 1, 2, 1, 0, 4})));
        Assert.assertFalse(Arrays.equals(new int[]{0, 1, 2}, ArrayUtils.removeDuplicates(new int[]{0, 1, 3})));
    }

    @Test
    public void testRemoveDuplicatesDataEArrayComparatorOfEClassOfE() {
        Assert.assertEquals(ArrayUtils.removeDuplicates((String[]) null, null), ArrayUtils.asList());
        Assert.assertEquals(ArrayUtils.removeDuplicates(new String[]{"1"}, null), ArrayUtils.asList("1"));
        Assert.assertEquals(ArrayUtils.removeDuplicates(new String[]{"1", "1"}, null), ArrayUtils.asList("1"));
        Assert.assertEquals(ArrayUtils.removeDuplicates(new String[]{"1", "1"}, new StringComparator()), ArrayUtils.asList("1"));
        Assert.assertEquals(ArrayUtils.removeDuplicates(new String[]{"1", "2", "3"}, new StringComparator()), ArrayUtils.asList("1", "2", "3"));
        Assert.assertEquals(ArrayUtils.removeDuplicates(new String[]{"1", "2", "3", "1", "1", "3", "3", "2", "2"}, new StringComparator()), ArrayUtils.asList("1", "2", "3"));
    }

    @Test
    public void testIsEmptyTArray() {
        Assert.assertTrue(ArrayUtils.isEmpty(null));
        Assert.assertTrue(ArrayUtils.isEmpty(new String[]{}));
        Assert.assertFalse(ArrayUtils.isEmpty(new String[]{""}));
        Assert.assertTrue(ArrayUtils.isEmpty(new int[0]));
        Assert.assertTrue(ArrayUtils.isEmpty(new boolean[]{}));
    }

    @Test
    public void testasList() {
        ArrayList<String> list = ArrayUtils.asList();
        Assert.assertTrue(list.isEmpty());

        String[] array = null;
        Assert.assertNull(ArrayUtils.asList(array));
    }

    @Test
    public void testToListListOfQ() {
        Assert.assertEquals(0, ArrayUtils.as().length);
        Assert.assertEquals(1, ArrayUtils.as("").length);
        Assert.assertEquals(2, ArrayUtils.as("1", "2").length);
        Assert.assertEquals("2", ArrayUtils.as("1", "2")[1]);
    }

    @Test
    public void testFirstListOfT() {
        Assert.assertNull(ArrayUtils.first(null));
        Assert.assertNull(ArrayUtils.first(new String[0]));
        Assert.assertEquals("1", ArrayUtils.first(new String[]{"1"}));
        Assert.assertEquals("1", ArrayUtils.first(new String[]{"1", "2"}));
        int value = ArrayUtils.first(new int[]{11, 2});
        Assert.assertEquals(11, value);
    }

    @Test
    public void testLastTArray() {
        String[] array = null;
        Assert.assertNull(ArrayUtils.last(array));

        array = new String[0];
        Assert.assertNull(ArrayUtils.last(array));

        array = new String[]{"1"};
        Assert.assertEquals(ArrayUtils.last(array), "1");

        array = new String[]{"1", "2"};
        Assert.assertEquals(ArrayUtils.last(array), "2");
    }

    @Test
    public void testFirst() {
        String[] array = null;
        ArrayUtils.first(array, "1");
        Assert.assertNull(ArrayUtils.first(array));

        array = new String[]{""};
        ArrayUtils.first(array, "1");
        Assert.assertEquals(ArrayUtils.first(array), "1");

        array = new String[]{"", "2"};
        ArrayUtils.first(array, "1");
        Assert.assertEquals(ArrayUtils.first(array), "1");
    }

    @Test
    public void testRemoveStrFromArray() {
        String[] array = (String[]) ArrayUtils.remove(new String[]{"", "1", "2"}, "1");
        Assert.assertTrue(array.length == 2 && array[0].equals("") && array[1].equals("2"));

        array = (String[]) ArrayUtils.remove(new String[]{"", "1", "2"}, "3");
        Assert.assertTrue(array.length == 3 && array[0].equals("") && array[1].equals("1") && array[2].equals("2"));

        Assert.assertArrayEquals(new String[]{"1"}, (Object[]) ArrayUtils.remove(new String[]{null, "1", null}, null));

        array = (String[]) ArrayUtils.remove(new String[]{"", "1", "2"}, "");
        Assert.assertTrue(array.length == 2 && array[0].equals("1") && array[1].equals("2"));

        String[] newArray = (String[]) ArrayUtils.remove(new String[]{"a", "b", "abc"}, "abc");
        Assert.assertEquals(-1, StringUtils.indexOf(newArray, "abc", 0, newArray.length, true));

        String[] a1 = (String[]) ArrayUtils.remove(new String[]{"a"}, "a");
        Assert.assertEquals(-1, StringUtils.indexOf(a1, "a", 0, a1.length, true));

        Assert.assertNull(ArrayUtils.remove(null, ""));
        Assert.assertArrayEquals(new String[]{}, (Object[]) ArrayUtils.remove(new String[]{}, ""));

        int[] array1 = (int[]) ArrayUtils.remove(new int[]{0, 1, 2}, 1);
        Assert.assertEquals(2, array1.length);
        Assert.assertEquals(0, array1[0]);
        Assert.assertEquals(2, array1[1]);
    }

    @Test
    public void testSubArray() {
        Assert.assertNull(ArrayUtils.subArray(null, 0, 0));
        Assert.assertArrayEquals(ArrayUtils.subArray(new String[]{}, 0, 0), new String[0]);

        String[] strarray = {null, "1", "2", "3", "4", "5"};
        Assert.assertArrayEquals(ArrayUtils.subArray(strarray, 0, 0), new String[0]);
        Assert.assertArrayEquals(ArrayUtils.subArray(strarray, 0, 1), new String[]{(String) null});
        Assert.assertArrayEquals(ArrayUtils.subArray(strarray, 0, 2), new String[]{null, "1"});
        Assert.assertArrayEquals(ArrayUtils.subArray(strarray, 0, 3), new String[]{null, "1", "2"});
        Assert.assertArrayEquals(ArrayUtils.subArray(strarray, 0, 4), new String[]{null, "1", "2", "3"});
        Assert.assertArrayEquals(ArrayUtils.subArray(strarray, 0, 5), new String[]{null, "1", "2", "3", "4"});

        String[] array = ArrayUtils.subArray(new String[]{"0", "1", "2"}, 0, 1);
        Assert.assertTrue(array.length == 1 && array[0].equals("0"));

        Assert.assertArrayEquals(ArrayUtils.subArray(new int[]{0, 1, 2, 3, 4, 5}, 0, 4), new int[]{0, 1, 2, 3});
    }

    @Test
    public void testJoinTArrayArray() {
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1", "2"}, 0, null), ArrayUtils.asList("0", "1", "2"));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1", "2"}, 0, new String[]{}), ArrayUtils.asList("0", "1", "2"));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1", "2"}, 0, new String[]{"-1"}), ArrayUtils.asList(new String[]{"-1", "0", "1", "2"}));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1", "2"}, 0, new String[]{"test"}), ArrayUtils.asList(new String[]{"test", "0", "1", "2"}));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0"}, 0, new String[]{"test"}), ArrayUtils.asList(new String[]{"test", "0"}));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1"}, 1, new String[]{"test"}), ArrayUtils.asList(new String[]{"0", "test", "1"}));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1"}, 1, new String[]{"test"}), ArrayUtils.asList(new String[]{"0", "test", "1"}));

        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1"}, 1, new String[]{"test", "3"}), ArrayUtils.asList(new String[]{"0", "test", "3", "1"}));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1"}, 2, new String[]{"test", "3"}), ArrayUtils.asList(new String[]{"0", "1", "test", "3"}));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1"}, 2, new String[]{"2"}), ArrayUtils.asList(new String[]{"0", "1", "2"}));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1"}, 2, new String[]{}), ArrayUtils.asList(new String[]{"0", "1"}));
        Assert.assertEquals(ArrayUtils.join(new String[]{"0", "1"}, 2, null), ArrayUtils.asList(new String[]{"0", "1"}));
    }

    @Test
    public void testCloneStringArray() {
        String[] array = ArrayUtils.copyOf(new String[]{"0", "1", "2"}, 3);
        Assert.assertEquals(new String[]{"0", "1", "2"}, array);
    }

    @Test
    public void testcopyOf() {
        String[] array = null;
        Assert.assertNull(ArrayUtils.copyOf(array, 1));

        array = new String[0];
        Assert.assertTrue(ArrayUtils.equals(ArrayUtils.copyOf(array, 0), new String[0], null));

        array = new String[]{"0"};
        Assert.assertTrue(ArrayUtils.equals(ArrayUtils.copyOf(array, 0), new String[0], null));

        array = new String[]{"0"};
        Assert.assertTrue(ArrayUtils.equals(ArrayUtils.copyOf(array, 1), new String[]{"0"}, null));

        array = new String[]{"0", "1", null};
        Assert.assertTrue(ArrayUtils.equals(ArrayUtils.copyOf(array, 2), new String[]{"0", "1"}, null));

        array = new String[]{"0", "1", null};
        Assert.assertTrue(ArrayUtils.equals(ArrayUtils.copyOf(array, 3), new String[]{"0", "1", null}, null));
    }

    @Test
    public void testtoindexOf() {
        Object[] array = new Object[]{1, "str", 123.12, null, true, null};
        Assert.assertEquals(3, ArrayUtils.indexOf(array, 0, null));
        Assert.assertEquals(5, ArrayUtils.indexOf(array, 4, null));
        Assert.assertEquals(1, ArrayUtils.indexOf(array, 0, "str"));
        Assert.assertEquals(-1, ArrayUtils.indexOf(array, 0, "obj"));
    }

    @Test
    public void test100() {
        int[] array = new int[0];
        Assert.assertEquals(1, ArrayUtils.shift(array).length);
        Assert.assertEquals(0, ArrayUtils.shift(array)[0]);

        array = new int[]{1};
        Assert.assertEquals(2, ArrayUtils.shift(array).length);
        Assert.assertEquals(1, ArrayUtils.shift(array)[1]);

        array = new int[]{1, 2};
        Assert.assertEquals(3, ArrayUtils.shift(array).length);
        Assert.assertEquals(2, ArrayUtils.shift(array)[2]);
    }
}
