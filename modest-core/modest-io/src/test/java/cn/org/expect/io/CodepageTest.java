package cn.org.expect.io;

import cn.org.expect.ioc.DefaultEasyContext;
import org.junit.Assert;
import org.junit.Test;

public class CodepageTest {

    @Test
    public void test() {
        DefaultEasyContext context = new DefaultEasyContext("sout+:info");
        Codepage bean = context.getBean(Codepage.class);
        Assert.assertEquals("UTF-8", bean.get("1208"));
        Assert.assertEquals("UTF-8", bean.get(1208));
        Assert.assertEquals("GBK", bean.get(1386));
        Assert.assertEquals("1208", bean.get("UTF-8"));
        Assert.assertEquals("1386", bean.get("GBK"));
    }

}
