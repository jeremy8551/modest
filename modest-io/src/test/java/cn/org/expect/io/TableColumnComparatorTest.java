package cn.org.expect.io;

import org.junit.Assert;
import org.junit.Test;

public class TableColumnComparatorTest {

    @Test
    public void test() {
        TableColumnComparator comparator = new TableColumnComparator();
        Assert.assertEquals(0, comparator.compare("1", "1"));
        Assert.assertNotEquals(0, comparator.compare("1", "2"));
        Assert.assertTrue(comparator.compare("1", "2") < 0);
        Assert.assertTrue(comparator.compare("2", "1") > 0);
    }
}
