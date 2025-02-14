package cn.org.expect.io;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class TextTableFileBuilderTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws Exception {
        TextTableFileFactory builder = new TextTableFileFactory();
        Assert.assertNotNull(builder.build(this.context, "csv"));
    }
}
