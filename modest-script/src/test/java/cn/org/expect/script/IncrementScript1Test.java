package cn.org.expect.script;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.TimeWatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 增量剥离测试：第一个与最后一个字段作为联合唯一索引, 测试有重复数据时测试是否能正确抛出异常
 */
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class IncrementScript1Test {
    private final static Log log = LogFactory.getLog(IncrementScript1Test.class);

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        TimeWatch watch = new TimeWatch();
        TextTableFile txt = this.context.getBean(TextTableFile.class, "txt");

        File parent = FileUtils.getTempDir(this.getClass().getSimpleName());
        FileUtils.assertClearDirectory(parent);
        File tmpfile = FileUtils.createNewFile(parent, ".txt");
        FileUtils.delete(tmpfile);
        FileUtils.createFile(tmpfile);
        txt.setAbsolutePath(tmpfile.getAbsolutePath());

        Random random = new Random();
        int line = random.nextInt(500);
        int next = 40000 + random.nextInt(9000);
        String copy = null;

        log.info("复制文件 {} 中第 {} 行到第 {} 行", tmpfile.getAbsoluteFile(), line, next);

        TextTableFileWriter out = txt.getWriter(false, IO.getCharArrayLength());
        for (int i = 1; i <= 50000; i++) {
            // 在指定行写入重复行
            if (i == next) {
                out.addLine(copy);
                continue;
            }

            StringBuilder buf = new StringBuilder();
            for (int j = 0; j < 20; j++) {
                buf.append(StringUtils.right(i + j, 8, ' '));
                buf.append(txt.getDelimiter());
            }
            out.addLine(buf.toString());

            // 复制指定行内容
            if (i == line) {
                copy = buf.toString();
            }

            if (i % 200 == 0) {
                out.flush();
            }
        }
        out.flush();
        out.close();

        log.info("");
        log.info("第 {} 行: {}", line, FileUtils.readline(tmpfile, CharsetUtils.get(), line));
        log.info("第 {} 行: {}", next, FileUtils.readline(tmpfile, CharsetUtils.get(), next));

        log.info("");
        log.info("创建临时文件 {} 用时: {}", tmpfile.getAbsolutePath(), watch.useTime());

        // ---------------------------------------------------------------------------------------------------------------------------------------------------------
        //
        // 对文件进行排序
        //
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------

        int column = txt.countColumn(); // 总列数

        File newfile = FileUtils.allocate(tmpfile.getParentFile(), "NEWFILE.txt");
        File oldfile = FileUtils.allocate(tmpfile.getParentFile(), "OLDFILE.txt");

        Assert.assertTrue(FileUtils.copy(tmpfile, newfile));
        Assert.assertTrue(FileUtils.copy(tmpfile, oldfile));
        FileUtils.assertDelete(tmpfile);

        File logfile = new File(newfile.getParentFile(), FileUtils.changeFilenameExt(newfile.getName(), "log"));
        File incfile = new File(newfile.getParentFile(), "INC_" + newfile.getName());

        log.info("");
        log.info(FileUtils.readline(newfile, CharsetUtils.get(), 1));
        log.info(FileUtils.readline(oldfile, CharsetUtils.get(), 1));
        log.info("");
        log.info("新文件: " + newfile);
        log.info("旧文件: " + oldfile);
        log.info("增量文件: " + incfile.getAbsolutePath());
        log.info("日志文件: " + logfile.getAbsolutePath());
        log.info("");

        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        try {
            // 设置命令中使用的文件路径与索引字段位置信息
            engine.evaluate("set newfile='" + newfile.getAbsolutePath() + "'");
            engine.evaluate("set oldfile='" + oldfile.getAbsolutePath() + "'");
            engine.evaluate("set incfile='" + incfile.getAbsolutePath() + "'");
            engine.evaluate("set index='1," + column + "'");
            engine.evaluate("set compare=");

            // 增量剥离命令
            String inccmd = "";
            inccmd += "extract increment compare ";
            inccmd += " $newfile of txt modified by index=$index ";
            inccmd += " and";
            inccmd += " $oldfile of txt modified by index=$index ";
            inccmd += " write new and upd and del into $incfile ";
            inccmd += " write log into " + logfile.getAbsolutePath();
            engine.evaluate(inccmd);
            Assert.fail();
        } catch (Throwable e) {
            log.info(e.getLocalizedMessage());
            String message = StringUtils.toString(e);
            String[] array = StringUtils.splitByBlank(StringUtils.trimBlank(message));
            Assert.assertTrue(StringUtils.inArrayIgnoreCase(String.valueOf(line), array));
            Assert.assertTrue(StringUtils.inArrayIgnoreCase(String.valueOf(next), array));
        }
    }
}
