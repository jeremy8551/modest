package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.DefaultEasyContext;
import org.junit.Assert;
import org.junit.Test;

public class EasyBeanFactoryImplTest {

    @Test
    public void test() {
        DefaultEasyContext context = new DefaultEasyContext("sout:info");
        CeshiBean bean = context.createBean(CeshiBean.class);
        Assert.assertNotNull(bean.getContext());
        Assert.assertEquals(context, bean.getContext());
        Assert.assertEquals("test2", bean.getCeshi().getMessage());
    }
}