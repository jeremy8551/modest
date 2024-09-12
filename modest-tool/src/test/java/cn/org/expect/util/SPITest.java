package cn.org.expect.util;

import java.util.List;

import cn.org.expect.impl.AttributeImpl;
import cn.org.expect.impl.pkg.Attribute1Impl;
import cn.org.expect.impl.test.Attribute2Impl;
import org.junit.Assert;
import org.junit.Test;

public class SPITest {

    @Test
    public void test1() {
        List<Attribute> it = SPI.load(ClassUtils.getDefaultClassLoader(), Attribute.class);
        Assert.assertEquals(AttributeImpl.class, it.get(0).getClass());
        Assert.assertEquals(Attribute2Impl.class, it.get(1).getClass());
        Assert.assertEquals(Attribute1Impl.class, it.get(2).getClass());
    }
}
