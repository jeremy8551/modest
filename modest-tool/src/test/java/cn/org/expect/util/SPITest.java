package cn.org.expect.util;

import java.util.List;

import cn.org.expect.impl.AttributeImpl;
import cn.org.expect.impl.pkg.Attribute1Impl;
import cn.org.expect.impl.test.Attribute2Impl;
import cn.org.expect.impl.test.Attribute3Impl;
import cn.org.expect.log.BufferLog;
import cn.org.expect.log.JUL;
import org.junit.Assert;
import org.junit.Test;

public class SPITest {

    @Test
    public void test() {
        try {
            BufferLog log = new BufferLog();
            Logs.setLogger(log);

            List<Attribute> list = SPI.load(ClassUtils.getClassLoader(), Attribute.class);
            Assert.assertEquals(AttributeImpl.class, list.get(0).getClass());
            Assert.assertEquals(Attribute1Impl.class, list.get(1).getClass());
            Assert.assertEquals(Attribute2Impl.class, list.get(2).getClass());
            Assert.assertNotEquals(-1, log.toString().indexOf(Attribute3Impl.class.getName()));
        } finally {
            Logs.setLogger(JUL.out);
        }
    }
}
