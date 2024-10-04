package cn.org.expect.script;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileCounter;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.TimeWatch;
import org.junit.Assert;
import org.junit.Test;

public class ScriptWcCommandTest {
    private final static Log log = LogFactory.getLog(ScriptWcCommandTest.class);

    /**
     * 测试 wc 命令
     */
    @Test
    public void test1() throws IOException {
        TimeWatch watch = new TimeWatch();
        EasyContext ioc = new DefaultEasyContext("debug:sout+", EasyBeanInfo.class.getPackage().getName() + ":info");
        TextTableFile txt = ioc.getBean(TextTableFile.class, "txt");

        File parent = FileUtils.getTempDir("test", ScriptWcCommandTest.class.getSimpleName());
        FileUtils.assertClearDirectory(parent);
        File tmpfile = FileUtils.createNewFile(parent, ".txt");
        txt.setAbsolutePath(tmpfile.getAbsolutePath());

        TextTableFileWriter out = txt.getWriter(false, IO.FILE_BYTES_BUFFER_SIZE);
        int rows = 500000 + new Random().nextInt(10000);
        for (int i = 1; i <= rows; i++) {
            StringBuilder buf = new StringBuilder();
            for (int j = 0; j < 20; j++) {
                buf.append(StringUtils.right(i + j, 8, ' '));
                buf.append(txt.getDelimiter());
            }
            out.addLine(buf.toString());

            if (i % 200 == 0) {
                out.flush();
            }
        }
        out.flush();
        out.close();

        log.info("");
        log.info("创建临时文件 {} 用时: {}", tmpfile.getAbsolutePath(), watch.useTime());

        // ---------------------------------------------------------------------------------------------------------------------------------------------------------
        //
        // 统计文件行数
        //
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------

        long unit = TextTableFileCounter.UNIT;
        try {
            TextTableFileCounter.UNIT = 1024;
            UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(ioc);
            UniversalScriptEngine engine;
            try {
                engine = factory.getScriptEngine();
                engine.eval("wc -l " + tmpfile.getAbsolutePath());
            } catch (Exception e1) {
                e1.printStackTrace();
                Assert.fail();
            }
        } finally {
            TextTableFileCounter.UNIT = unit;
        }
    }

}
