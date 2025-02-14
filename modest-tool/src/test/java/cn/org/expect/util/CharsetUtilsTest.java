package cn.org.expect.util;

import org.junit.Assert;
import org.junit.Test;

public class CharsetUtilsTest {

    @Test
    public void test1() {
        Assert.assertTrue(StringUtils.isNotBlank(CharsetUtils.get()));
        Assert.assertEquals("gbk", CharsetUtils.get("gbk"));
    }
}
