package cn.org.expect.script;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileCounter;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class ScriptWcCommandTest {
    private final static Log log = LogFactory.getLog(ScriptWcCommandTest.class);

    @EasyBean
    private EasyContext context;

    /**
     * 测试 wc 命令
     */
    @Test
    public void test1() throws IOException {
        File tempDir = FileUtils.getTempDir(this.getClass().getSimpleName());
        FileUtils.deleteDirectory(tempDir);
        FileUtils.createDirectory(tempDir);

        TextTableFile txt = this.context.getBean(TextTableFile.class, "txt");
        File tmpfile = FileUtils.createNewFile(tempDir, ".txt");
        txt.setAbsolutePath(tmpfile.getAbsolutePath());

        // 创建文件
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

        // ---------------------------------------------------------------------------------------------------------------------------------------------------------
        //
        // 统计文件行数
        //
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------

        long unit = TextTableFileCounter.UNIT;
        try {
            TextTableFileCounter.UNIT = 1024;
            UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(this.context);
            UniversalScriptEngine engine;
            try {
                engine = factory.getScriptEngine();
                engine.evaluate("wc -l " + tmpfile.getAbsolutePath() + " > /dev/null");
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                Assert.fail();
            }
        } finally {
            TextTableFileCounter.UNIT = unit;
        }
    }
}
