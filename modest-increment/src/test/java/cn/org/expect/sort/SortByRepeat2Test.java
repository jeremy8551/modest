package cn.org.expect.sort;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import cn.org.expect.concurrent.ThreadSource;
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
 * 文件中有重复数据，且重复数据所在行号是跨度比较大（需要在merge阶段检查重复数据）
 */
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class SortByRepeat2Test {
    private final static Log log = LogFactory.getLog(SortByRepeat2Test.class);

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        TimeWatch watch = new TimeWatch();
        TextTableFile txt = context.getBean(TextTableFile.class, "txt");

        File tmpfile = FileUtils.createTempFile(".txt");
        FileUtils.delete(tmpfile);
        FileUtils.createFile(tmpfile);
        txt.setAbsolutePath(tmpfile.getAbsolutePath());

        Random random = new Random();
        int line = random.nextInt(500);
        if (line == 0) {
            line = 1;
        }

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

        log.info("第 {} 行: {}", line, FileUtils.readline(tmpfile, CharsetUtils.get(), line));
        log.info("第 {} 行: {}", next, FileUtils.readline(tmpfile, CharsetUtils.get(), next));
        log.info("创建临时文件 {} 用时: {}", tmpfile.getAbsolutePath(), watch.useTime());

        // ---------------------------------------------------------------------------------------------------------------------------------------------------------
        //
        // 对文件进行排序
        //
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------

        File txtfile = new File(txt.getAbsolutePath());
        File bakfile = new File(txtfile.getParentFile(), FileUtils.changeFilenameExt(txtfile.getName(), "bak"));
        Assert.assertTrue(FileUtils.deleteFile(bakfile));
        Assert.assertTrue(FileUtils.copy(txtfile, bakfile));

        TableFileSortContext sortContext = new TableFileSortContext();
        sortContext.setThreadSource(context.getBean(ThreadSource.class));
        sortContext.setFileCount(2);
        sortContext.setThreadNumber(2);
        sortContext.setDeleteFile(true);
        sortContext.setKeepSource(true);
        sortContext.setReaderBuffer(1024 * 1024 * 100);
        sortContext.setWriterBuffer(1024 * 1024 * 100);
        sortContext.setMaxRows(1000);
        sortContext.setWriterBuffer(800);
        sortContext.setRemoveLastField(false);
        sortContext.setTempDir(null);

        TableFileSorter sorter = new TableFileSorter(sortContext);
        try {
            sorter.execute(context, txt, "int(1) desc");
            Assert.fail();
        } catch (Throwable e) {
            String message = StringUtils.toString(e);
            log.info("message: {}", message);
            String[] array = StringUtils.splitByBlank(StringUtils.trimBlank(message));
            Assert.assertTrue(StringUtils.inArrayIgnoreCase(String.valueOf(line), array));
            Assert.assertTrue(StringUtils.inArrayIgnoreCase(String.valueOf(next), array));
        }
    }
}
