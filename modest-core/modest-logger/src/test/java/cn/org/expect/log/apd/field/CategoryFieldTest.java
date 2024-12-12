package cn.org.expect.log.apd.field;

import cn.org.expect.log.LogFactory;
import cn.org.expect.log.PatternLogBuilder;
import cn.org.expect.log.apd.LogAppender;
import cn.org.expect.log.LogContextImpl;
import org.junit.Assert;
import org.junit.Test;

public class CategoryFieldTest {

    @Test
    public void test1() {
        LogContextImpl context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());

        LogAppender a = new LogAppender("%c{10}");
        a.setup(context);

        LogFactory.getLog(context, String.class, null, false).info("test");
        Assert.assertEquals(String.class.getName(), a.getValue());

        a.close();
        Assert.assertNotNull(a.getName());
    }
}
