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
public class CodepageTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() {
        Codepage bean = this.context.getBean(Codepage.class);
        Assert.assertEquals("UTF-8", bean.get("1208"));
        Assert.assertEquals("UTF-8", bean.get(1208));
        Assert.assertEquals("GBK", bean.get(1386));
        Assert.assertEquals("1208", bean.get("UTF-8"));
        Assert.assertEquals("1386", bean.get("GBK"));
    }
}
