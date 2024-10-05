package cn.org.expect.script;

import java.io.File;
import java.io.IOException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.db2.DB2ExportFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
public class DB2ExportFileReaderTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        File tempfile = FileUtils.createTempFile(".del");
        engine.eval("cp classpath:/bhc_finish.del " + tempfile.getAbsolutePath());
        DB2ExportFile file = new DB2ExportFile(tempfile);
        TextTableFileReader in = file.getReader(IO.READER_BUFFER_SIZE);
        TextTableLine line = null;
        while ((line = in.readLine()) != null) {
            if (in.getLineNumber() <= 20) {
                System.out.println(line.getContent());
            }
        }
        System.out.println("文件 " + tempfile.getAbsolutePath() + " 已读取 " + in.getLineNumber() + " 行数据!");
        in.close();
    }

}
