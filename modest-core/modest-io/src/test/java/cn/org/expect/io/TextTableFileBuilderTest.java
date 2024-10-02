package cn.org.expect.io;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyBeanInfo;
import org.junit.Assert;
import org.junit.Test;

public class TextTableFileBuilderTest {

    @Test
    public void test() throws Exception {
        DefaultEasyContext cxt = new DefaultEasyContext("sout:debug", EasyBeanInfo.class.getPackage().getName() + ":info");
        TextTableFileBuilder builder = new TextTableFileBuilder();
        Assert.assertNotNull(builder.getBean(cxt, "csv"));
    }

}
