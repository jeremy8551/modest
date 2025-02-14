package cn.org.expect.script;

import java.io.File;
import java.io.IOException;

import cn.org.expect.database.db2.DB2ExportFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class DB2ExportFileReaderTest {
    private final static Log log = LogFactory.getLog(DB2ExportFileReaderTest.class);

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        File tempfile = FileUtils.createTempFile(".del");
        engine.evaluate("cp classpath:/bhc_finish.del " + tempfile.getAbsolutePath());
        DB2ExportFile file = new DB2ExportFile(tempfile);
        TextTableFileReader in = file.getReader(IO.READER_BUFFER_SIZE);
        while (in.readLine() != null) {
        }
        log.info("文件 {} 已读取 {} 行数据!", tempfile, in.getLineNumber());
        Assert.assertEquals(2733, in.getLineNumber());
        in.close();
    }
}
