package cn.org.expect.log.apd;

import java.io.File;
import java.io.IOException;

import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogTest;
import cn.org.expect.log.file.FileAppender;
import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.PatternLogBuilder;
import cn.org.expect.util.CharsetName;
import org.junit.Assert;
import org.junit.Test;

public class FileAppenderTest {

    @Test
    public void test1() throws IOException {
        LogContext context = new LogContextImpl();
        context.setBuilder(new PatternLogBuilder());

        String pattern = "%d|%p|%level|%processId|%t|%l|%c|%C.%M(%F:%L)|mills=%r|%X{test}|%m%ex%n";
        FileAppender appender = new FileAppender(null, CharsetName.UTF_8, pattern, true);
        Assert.assertNotNull(appender.setup(context));

        LogFactory.getLog(context, LogTest.class).trace("test level is {}", "trace");

        String file = appender.getFile();
        Assert.assertTrue(new File(file).exists());
    }
}
