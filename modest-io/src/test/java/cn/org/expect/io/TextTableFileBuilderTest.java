package cn.org.expect.io;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:info")
public class TextTableFileBuilderTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws Exception {
        TextTableFileFactory builder = new TextTableFileFactory();
        Assert.assertNotNull(builder.build(this.context, "csv"));
    }
}
