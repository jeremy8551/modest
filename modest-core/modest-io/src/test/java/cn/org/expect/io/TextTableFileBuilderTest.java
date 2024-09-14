package cn.org.expect.io;

import cn.org.expect.ioc.DefaultEasyetlContext;
import cn.org.expect.ioc.EasyetlBean;
import org.junit.Assert;
import org.junit.Test;

public class TextTableFileBuilderTest {

    @Test
    public void test() throws Exception {
        DefaultEasyetlContext cxt = new DefaultEasyetlContext("sout:debug", EasyetlBean.class.getPackage().getName() + ":info");
        TextTableFileBuilder builder = new TextTableFileBuilder();
        Assert.assertNotNull(builder.getBean(cxt, "csv"));
    }

}
