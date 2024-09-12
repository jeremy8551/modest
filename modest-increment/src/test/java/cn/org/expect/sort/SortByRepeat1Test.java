package cn.org.expect.sort;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.ioc.DefaultEasyetlContext;
import cn.org.expect.ioc.EasyetlBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.TimeWatch;
import org.junit.Assert;
import org.junit.Test;

/**
 * 文件中有重复数据，且重复是连续在一起的（间隔小）
 */
public class SortByRepeat1Test {

    @Test
    public void test() throws Exception {
        TimeWatch watch = new TimeWatch();
        DefaultEasyetlContext ioc = new DefaultEasyetlContext("debug:sout+", EasyetlBean.class.getPackage().getName() + ":info");
        Log log = LogFactory.getLog(SortByRepeat1Test.class);

        TextTableFile txt = ioc.getBean(TextTableFile.class, "txt");

        File tmpfile = FileUtils.createTempFile(".txt");
        FileUtils.delete(tmpfile);
        FileUtils.createFile(tmpfile);
        txt.setAbsolutePath(tmpfile.getAbsolutePath());

        Random random = new Random();
        int line = random.nextInt(500);
        if (line == 0) {
            line = 1;
        }

        int next = line + random.nextInt(100);
        String copy = null;

        // 如果相等的话
        if (line == next) {
            next += 1;
        }

        log.info("复制文件 {} 中第 {} 行到第 {} 行", tmpfile.getAbsoluteFile(), line, next);

        TextTableFileWriter out = txt.getWriter(false, IO.FILE_BYTES_BUFFER_SIZE);
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
        log.info("第 {} 行: {}", line, FileUtils.readline(tmpfile, StringUtils.CHARSET, line));
        log.info("第 {} 行: {}", next, FileUtils.readline(tmpfile, StringUtils.CHARSET, next));

        log.info("");
        log.info("创建临时文件 {} 用时: {}", tmpfile.getAbsolutePath(), watch.useTime());

        // ---------------------------------------------------------------------------------------------------------------------------------------------------------
        //
        // 对文件进行排序
        //
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------

        File txtfile = new File(txt.getAbsolutePath());
        File bakfile = new File(txtfile.getParentFile(), FileUtils.changeFilenameExt(txtfile.getName(), "bak"));
        FileUtils.deleteFile(bakfile);
        Assert.assertTrue(FileUtils.copy(txtfile, bakfile));

        TableFileSortContext context = new TableFileSortContext();
        context.setThreadSource(ioc.getBean(ThreadSource.class));
        context.setFileCount(2);
        context.setThreadNumber(2);
        context.setDeleteFile(true);
        context.setKeepSource(true);
        context.setReaderBuffer(1024 * 1024 * 100);
        context.setWriterBuffer(1024 * 1024 * 100);
        context.setMaxRows(1000);
        context.setWriterBuffer(800);
        context.setRemoveLastField(false);
        context.setTempDir(null);

        TableFileDeduplicateSorter sorter = new TableFileDeduplicateSorter(context);
        try {
            sorter.execute(ioc, txt, "int(1) desc");
            Assert.fail();
        } catch (IOException e) {
            String message = e.getLocalizedMessage();
            log.info(message);
            String[] array = StringUtils.splitByBlank(message);
            Assert.assertTrue(StringUtils.inArrayIgnoreCase(String.valueOf(line), array));
            Assert.assertTrue(StringUtils.inArrayIgnoreCase(String.valueOf(next), array));
        }

    }

}
