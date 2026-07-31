package cn.org.expect.util;

import org.junit.Assert;
import org.junit.Test;

public class NetUtilsTest {

    @Test
    public void test() {
        Assert.assertTrue(NetUtils.joinUri("/", "/").equals("/"));
        Assert.assertTrue(NetUtils.joinUri("/1", "/").equals("/1/"));
        Assert.assertTrue(NetUtils.joinUri("/", "/1").equals("/1"));
        Assert.assertTrue(NetUtils.joinUri("/a", "/b").equals("/a/b"));
        Assert.assertTrue(NetUtils.joinUri("/a", "/b").equals("/a/b"));
        Assert.assertTrue(NetUtils.joinUri("a", "b").equals("a/b"));
        Assert.assertTrue(NetUtils.joinUri("a/", "/b").equals("a/b"));
        Assert.assertTrue(NetUtils.joinUri(null, null).equals(""));
        Assert.assertTrue(NetUtils.joinUri("/", null).equals("/"));
        Assert.assertTrue(NetUtils.joinUri(null, "/").equals("/"));
    }

    @Test
    public void testparseContentTypeCharset() {
        Assert.assertEquals("gbk", NetUtils.parseContentTypeCharset("application/soap+xml; charset=gbk"));
        Assert.assertEquals("gbk", NetUtils.parseContentTypeCharset("application/soap+xml; charset= gbk"));
        Assert.assertEquals("gbk", NetUtils.parseContentTypeCharset("application/soap+xml; charset = gbk"));
        Assert.assertEquals("gbk", NetUtils.parseContentTypeCharset("application/soap+xml; charset = gbk "));
        Assert.assertEquals("gbk", NetUtils.parseContentTypeCharset("application/soap+xml; charset =  gbk "));
        Assert.assertEquals("gbk", NetUtils.parseContentTypeCharset("application/soap+xml; charset =  gbk"));
        Assert.assertNull(NetUtils.parseContentTypeCharset("application/soap+xml; charset =   "));
        Assert.assertNull(NetUtils.parseContentTypeCharset("application/soap+xml;  =   "));
        Assert.assertNull(NetUtils.parseContentTypeCharset("application/soap+xml;  charset  "));
        Assert.assertNull(NetUtils.parseContentTypeCharset("application/soap+xml;  charset gbk "));
    }
}
