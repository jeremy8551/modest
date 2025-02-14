package cn.org.expect.os;

import cn.org.expect.os.internal.OSCommandUtils;
import org.junit.Assert;
import org.junit.Test;

public class OSCommandUtilsTest {

    @Test
    public void test() {
        String str = OSCommandUtils.START_PREFIX + "test\n1\n2\n3\n" + OSCommandUtils.START_PREFIX + "key";
        OSCommandStdouts map = OSCommandUtils.splitMultiCommandStdout(str);
        Assert.assertTrue(map.get("key").isEmpty());
        Assert.assertEquals(3, map.get("test").size());

        str = OSCommandUtils.START_PREFIX + "test\n1\n2\n3\n";
        map = OSCommandUtils.splitMultiCommandStdout(str);
        Assert.assertNull(map.get("key"));
        Assert.assertEquals(3, map.get("test").size());
        Assert.assertEquals("1", map.get("test").get(0));
        Assert.assertEquals("2", map.get("test").get(1));
        Assert.assertEquals("3", map.get("test").get(2));
    }
}
