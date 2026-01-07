package cn.org.expect.ioc;

import java.util.Arrays;

import cn.org.expect.ioc.internal.ScanPatternList;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Settings;
import org.junit.Assert;
import org.junit.Test;

public class EasyScanPatternListTest {
    private final static Log log = LogFactory.getLog(EasyScanPatternListTest.class);

    @Test
    public void test1() {
        ScanPatternList list = new ScanPatternList();
        list.add("org.apache.test");
        list.add("org.apache.*");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("org.apache", list.get(0).getRule());

        list.add("!com.spring.**");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("!com.spring", list.get(1).getRule());

        list.addArgument("a.b.c,a.b,", "!", "^^");
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("a.b", list.get(2).getRule());
        Assert.assertEquals(2, list.getScanPattern().size());

        list.exclude(Arrays.asList("d.c"));
        Assert.assertEquals(2, list.getScanPattern().size());
        Assert.assertEquals("!d.c", list.get(list.size() - 1).getRule());
        Assert.assertTrue(list.get(list.size() - 1).isExclude());

        System.setProperty(EasyClassScanner.PROPERTY_SCAN_PKG, "g.h.c, !a.c.d");
        list.addProperty();
        Assert.assertEquals(3, list.getScanPattern().size());
        Assert.assertEquals("!a.c.d", list.get(list.size() - 1).getRule());

        int old = list.size();
        list.addFirst("org.apache.test");
        Assert.assertEquals(old, list.size());
        Assert.assertEquals("org.apache", list.get(0).getPrefix());

        list.addGroupID();
        Assert.assertEquals(old + 1, list.size());
        Assert.assertEquals(Settings.getPackageName(), list.get(0).getPrefix());

        Assert.assertEquals("cn.org.expect,org.apache,!com.spring,a.b,!d.c,g.h.c,!a.c.d", list.toArgumentString());
        Assert.assertEquals("[cn.org.expect, org.apache, !com.spring, a.b, !d.c, g.h.c, !a.c.d]", Arrays.toString(list.toArray()));
        log.info(list.toArgumentString());
        log.info(Arrays.toString(list.toArray()));
    }
}
