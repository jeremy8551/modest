package cn.org.expect.ioc;

import cn.org.expect.ioc.internal.ScanPattern;
import org.junit.Assert;
import org.junit.Test;

public class EasyScanPatternTest {

    @Test
    public void test() {
        ScanPattern pattern = new ScanPattern("a");
        Assert.assertTrue(!pattern.isBlank());

        pattern = new ScanPattern("org.test");
        Assert.assertFalse(pattern.isBlank());
        Assert.assertEquals("org.test", pattern.getPrefix());
        Assert.assertEquals("org.test", pattern.getRule());
        Assert.assertFalse(pattern.isExclude());
        Assert.assertEquals("org.test", pattern.parse("org.test.*?.ab.cde"));
    }

    @Test
    public void test1() {
        ScanPattern pattern = new ScanPattern("!");
        Assert.assertTrue(pattern.isBlank());

        pattern = new ScanPattern("^^^");
        Assert.assertTrue(pattern.isBlank());

        pattern = new ScanPattern("!org.test");
        Assert.assertFalse(pattern.isBlank());
        Assert.assertEquals("org.test", pattern.getPrefix());
        Assert.assertEquals("!org.test", pattern.getRule());
        Assert.assertTrue(pattern.isExclude());

        Assert.assertEquals("org.test", pattern.parse("org.test.*?.ab.cde"));
        Assert.assertEquals("org.test", pattern.parse("org.test.*?.*.?"));
        Assert.assertEquals("org.test", pattern.parse("org.test."));
        Assert.assertEquals("org.test", pattern.parse("org.test.."));
    }

    @Test
    public void test2() {
        Assert.assertTrue(new ScanPattern("!a.b").contains(new ScanPattern("!a.b.c")));
        Assert.assertTrue(new ScanPattern("!a").contains(new ScanPattern("!a.b.c")));
        Assert.assertFalse(new ScanPattern("a").contains(new ScanPattern("!a.b.c")));
        Assert.assertTrue(new ScanPattern("a").contains(new ScanPattern("a.b.c")));
        Assert.assertTrue(new ScanPattern("a").contains(new ScanPattern("a.b")));
        Assert.assertTrue(new ScanPattern("a").contains(new ScanPattern("a.b.c.d.e")));
    }
}
