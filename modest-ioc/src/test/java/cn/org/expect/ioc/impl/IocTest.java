package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.util.ClassUtils;
import org.junit.Assert;
import org.junit.Test;

public class IocTest {

    @Test
    public void test() {
        DefaultEasyContext context = new DefaultEasyContext("sout:info", ClassUtils.getPackageName(IocTest.class, 3));
        CeshiBean bean = context.newInstance(CeshiBean.class);
        Assert.assertNotNull(bean.getContext());
        Assert.assertEquals(context, bean.getContext());
        Assert.assertEquals("test1", bean.getCeshi1().getMessage());
        Assert.assertEquals("test2", bean.getCeshi2().getMessage());
        Assert.assertEquals("test1", bean.getAware().getMessage());
    }
}
