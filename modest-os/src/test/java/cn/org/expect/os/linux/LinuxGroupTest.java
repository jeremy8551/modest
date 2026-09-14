package cn.org.expect.os.linux;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link LinuxGroup} 的单元测试。
 */
public class LinuxGroupTest {

    @Test
    public void testToStringDoesNotExposePassword() {
        LinuxGroup group = new LinuxGroup();
        group.setName("operators");
        group.setPassword("top-secret");
        group.setGid("1000");
        group.addUser("alice");

        String value = group.toString();
        Assert.assertFalse(value.contains("top-secret"));
        Assert.assertTrue(value.contains("password=******"));
        Assert.assertTrue(value.contains("operators"));
        Assert.assertTrue(value.contains("alice"));
    }

    @Test
    public void testEmptyAndNullPasswordAreStillRedacted() {
        LinuxGroup group = new LinuxGroup();
        Assert.assertFalse(group.toString().contains("password=null"));

        group.setPassword("");
        Assert.assertFalse(group.toString().contains("password=,"));
    }
}
