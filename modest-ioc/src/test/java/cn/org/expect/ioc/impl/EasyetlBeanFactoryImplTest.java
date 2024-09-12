package cn.org.expect.ioc.impl;

import cn.org.expect.ioc.DefaultEasyetlContext;
import org.junit.Assert;
import org.junit.Test;

public class EasyetlBeanFactoryImplTest {

    @Test
    public void test() {
        DefaultEasyetlContext context = new DefaultEasyetlContext("sout:info");
        CeshiBean bean = context.createBean(CeshiBean.class);
        Assert.assertNotNull(bean.getContext());
        Assert.assertEquals(context, bean.getContext());
        Assert.assertEquals("test2", bean.getCeshi().getMessage());
    }

}