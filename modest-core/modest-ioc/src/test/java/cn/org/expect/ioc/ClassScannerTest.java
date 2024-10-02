package cn.org.expect.ioc;

import java.util.List;

import cn.org.expect.test.bean.TestLoader;
import cn.org.expect.test.impl.TestLoader2;
import cn.org.expect.test.impl.sec1.TestLoader1;
import org.junit.Assert;
import org.junit.Test;

public class ClassScannerTest {

    @Test
    public void test() {
        DefaultEasyContext context = new DefaultEasyContext("sout:debug,!org.apache,!cn.org.expect.test.impl.sec1,");
        List<EasyBeanInfo> list = context.getBeanInfoList(TestLoader.class);
        boolean exists = false;
        boolean exists1 = false;
        for (EasyBeanInfo anno : list) {
            if (anno.getType().equals(TestLoader1.class)) {
                exists = true;
            }
            if (anno.getType().equals(TestLoader2.class)) {
                exists1 = true;
            }
        }
        Assert.assertFalse(exists);
        Assert.assertTrue(exists1);

        int num = context.scanPackages(TestLoader1.class.getPackage().getName(), ":info");
        Assert.assertEquals(1, num);
        exists = false;
        exists1 = false;
        list = context.getBeanInfoList(TestLoader.class);
        for (EasyBeanInfo anno : list) {
            if (anno.getType().equals(TestLoader1.class)) {
                exists = true;
            }
            if (anno.getType().equals(TestLoader2.class)) {
                exists1 = true;
            }
        }
        Assert.assertTrue(exists);
        Assert.assertTrue(exists1);
    }

}
