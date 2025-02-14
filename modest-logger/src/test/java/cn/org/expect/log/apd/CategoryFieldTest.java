package cn.org.expect.log.apd;

import cn.org.expect.log.Appender;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.internal.LogBuilderAppender;
import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.PatternConsoleAppender;
import cn.org.expect.log.internal.PatternLogBuilder;
import org.junit.Assert;
import org.junit.Test;

public class CategoryFieldTest {

    @Test
    public void test1() {
        LogContextImpl context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());
        context.removeAppender(PatternConsoleAppender.class);
        Appender appender = new LogBuilderAppender("%c{10}").setup(context);
        LogFactory.getLog(context, String.class, null, false).info("test");
        Assert.assertEquals(String.class.getName(), appender.toString());
        Assert.assertNotNull(appender.getName());
    }
}
