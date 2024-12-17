package cn.org.expect.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM) // 控制JUnit单元测试方法执行顺序
@RunWith(ModestRunner.class)
public class SortTableFileCommandTest {

    @EasyBean
    private EasyContext context;

    /**
     * 测试倒序排序
     */
    @Test
    public void test1() throws IOException {
        EasyContext context = new DefaultEasyContext();
        TextTableFile file = context.getBean(TextTableFile.class, "txt");
        file.setDelimiter(",");
        File txtfile = this.createfile(file);
        file.setAbsolutePath(txtfile.getAbsolutePath());

        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        try {
            engine.evaluate("sort table file " + txtfile.getAbsolutePath() + " of txt modified by thread=3 maxrow=10000 maxfile=3 covsrc order by int(1) asc,2 desc");
            this.checkFile(file);
        } catch (Exception e1) {
            e1.printStackTrace();
            Assert.fail();
        } finally {
            engine.evaluate("exit 0");
        }
    }

    protected void checkFile(TextTableFile file) throws NumberFormatException, IOException {
        int i = 0;
        TextTableFileReader in = file.getReader(IO.FILE_BYTES_BUFFER_SIZE);
        TextTableLine line;
        while ((line = in.readLine()) != null) {
            if (++i != Integer.parseInt(StringUtils.trimBlank(line.getColumn(1)))) {
                Assert.fail(i + " != " + Integer.parseInt(StringUtils.trimBlank(line.getColumn(1))));
            }

            if (i + 19 != Integer.parseInt(StringUtils.trimBlank(line.getColumn(20)))) {
                Assert.fail((i + 19) + " != " + StringUtils.trimBlank(line.getColumn(20)));
            }
        }
        in.close();
    }

    protected File createfile(TextTableFile tableFile) throws IOException {
        File file = FileUtils.createTempFile("SortTableFile.txt");
        FileUtils.clearDirectory(file.getParentFile());
        FileUtils.createFile(file);

        FileWriter out = new FileWriter(file);
        StringBuilder buf = new StringBuilder();
        for (int i = 50000; i > 0; i--) {
            for (int j = 0; j < 20; j++) {
                buf.append(StringUtils.right(i + j, 8, ' '));
                buf.append(tableFile.getDelimiter());
            }
            buf.append(tableFile.getLineSeparator());
            out.write(buf.toString());
            buf.setLength(0);

            if (i % 20 == 0) {
                out.flush();
            }
        }
        out.flush();
        out.close();

        System.out.println("sort file: " + file.getAbsolutePath());
        return file;
    }

}
