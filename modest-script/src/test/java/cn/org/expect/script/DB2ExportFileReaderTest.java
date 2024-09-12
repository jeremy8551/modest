package cn.org.expect.script;

import java.io.File;
import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cn.org.expect.database.db2.DB2ExportFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import org.junit.Test;

public class DB2ExportFileReaderTest {

    @Test
    public void test() throws ScriptException, IOException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
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
