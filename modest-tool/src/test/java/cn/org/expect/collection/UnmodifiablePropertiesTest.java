package cn.org.expect.collection;

import java.util.Properties;

import cn.org.expect.util.ClassUtils;
import org.junit.Assert;
import org.junit.Test;

public class UnmodifiablePropertiesTest {

    @Test
    public void test1() {
        Properties p = new Properties();
        p.setProperty("name", "test");

        UnmodifiableProperties unp = new UnmodifiableProperties(p);

        try {
            unp.setProperty("name", "");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        try {
            unp.remove("name");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        try {
            unp.load(ClassUtils.getClassLoader().getResourceAsStream("Messages.properties"));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }
}
