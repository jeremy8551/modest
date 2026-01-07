package cn.org.expect.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class StackTraceUtilsTest {

    @Test
    public void test1() {
        Assert.assertFalse(StringUtils.isNumber());
    }

    @Test
    public void test4() {
        Assert.assertFalse(StringUtils.isNumber("123a"));
        Assert.assertFalse(StringUtils.isNumber(""));
        Assert.assertFalse(StringUtils.isNumber((CharSequence) null));
    }

    @Test
    public void test5() {
        SQLException e = new SQLException();
        String str = StringUtils.toString(e);
        Assert.assertTrue(StringUtils.isNotBlank(str));
        Assert.assertNotEquals(-1, str.indexOf("java.sql.SQLException"));
        Assert.assertNotEquals(-1, str.indexOf("SQLSTATE"));
        Assert.assertNotEquals(-1, str.indexOf("ERRORCODE"));
        Assert.assertNotEquals(-1, str.indexOf("MESSAGE"));
    }

    @Test
    public void test6() {
        List<String> list = new ArrayList<String>();

        StringUtils.splitLines("", list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("", list.get(0));

        list.clear();
        StringUtils.splitLines("\n", list);
        Assert.assertEquals(1, list.size());

        list.clear();
        StringUtils.splitLines("\n\n", list);
        Assert.assertEquals(2, list.size());

        list.clear();
        StringUtils.splitLines("1\n2\n", list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("1", list.get(0));
        Assert.assertEquals("2", list.get(1));

        list.clear();
        StringUtils.splitLines("\r\n\r\n", list);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void test7() {
        Assert.assertNull(ClassUtils.forName(null));
        Assert.assertNull(ClassUtils.forName("?"));
    }
}
