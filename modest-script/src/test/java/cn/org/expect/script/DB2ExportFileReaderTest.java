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
import cn.org.expect.test.annotation.RunWithLogSettings;
import cn.org.expect.util.IO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:info")
public class DB2ExportFileReaderTest {
    private final static Log log = LogFactory.getLog(DB2ExportFileReaderTest.class);

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        File destFile = engine.evaluate("cp classpath:/bhc_finish.del $tmpdir");
        DB2ExportFile file = new DB2ExportFile(destFile);
        TextTableFileReader in = file.getReader(IO.getCharArrayLength());
        while (in.readLine() != null) {
            String path = destFile.getAbsolutePath();
            Assert.assertNotNull(path);
        }
        log.info("“File {} has read {} lines of data!”", destFile, in.getLineNumber());
        Assert.assertEquals(2733, in.getLineNumber());
        in.close();
    }
}
