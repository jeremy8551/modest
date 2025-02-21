package cn.org.expect.sort;

import java.io.File;
import java.io.IOException;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.io.TextTableFile;
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
public class TableFileTest {

    @EasyBean
    private EasyContext context;

    protected void createTestFile(TextTableFile file) throws IOException {
        File tmpfile = FileUtils.createTempFile(".txt");
        FileUtils.delete(tmpfile);
        FileUtils.createFile(tmpfile);
        file.setAbsolutePath(tmpfile.getAbsolutePath());

        TextTableFileWriter out = file.getWriter(false, IO.getCharArrayLength());
        for (int i = 50000; i > 0; i--) {
            StringBuilder buf = new StringBuilder();
            for (int j = 0; j < 20; j++) {
                buf.append(StringUtils.right(i + j, 8, ' '));
                buf.append(file.getDelimiter());
            }
            out.addLine(buf.toString());

            if (i % 20 == 0) {
                out.flush();
            }
        }
        out.flush();
        out.close();
    }

    @Test
    public void test() throws Exception {
        Log log = LogFactory.getLog(TableFileTest.class);

        TextTableFile file = this.context.getBean(TextTableFile.class, "txt");
        this.createTestFile(file);

        File txtfile = new File(file.getAbsolutePath());
        File bakfile = new File(txtfile.getParentFile(), FileUtils.changeFilenameExt(txtfile.getName(), "bak"));
        FileUtils.deleteFile(bakfile);
        Assert.assertTrue(FileUtils.copy(txtfile, bakfile));

        TableFileSortContext context = new TableFileSortContext();
        context.setThreadSource(this.context.getBean(ThreadSource.class));
        context.setFileCount(4);
        context.setThreadNumber(3);
        context.setDeleteFile(false);
        context.setKeepSource(true);
        context.setReaderBuffer(1024 * 1024 * 100);
        context.setWriterBuffer(1024 * 1024 * 100);
        context.setMaxRows(10000);
        context.setWriterBuffer(800);
        context.setRemoveLastField(true);

        log.info("排序前文件: " + file.getAbsolutePath() + ", 列数: " + file.getColumn());
        TableFileSorter sorter = new TableFileSorter(context);
        File sort = sorter.execute(this.context, file, "1 asc");
        log.info("排序后文件: " + sort.getAbsolutePath());

        // 再次排序
        TextTableFile sortfile = file.clone();
        sortfile.setAbsolutePath(sort.getAbsolutePath());
        sortfile.countColumn(); // 需要重新统计列数

        context.setDeleteFile(true);
        context.setKeepSource(true);
        context.setMaxRows((int) file.getFile().length());
        context.setRemoveLastField(true);

        File oldfile = sorter.execute(this.context, sortfile, "1 desc");
        log.info("再次排序后文件: " + oldfile.getAbsolutePath());
        log.info("");

        log.info(txtfile.getAbsolutePath());
        log.info(oldfile.getAbsolutePath());

        log.info(FileUtils.readline(txtfile, file.getCharsetName(), 1));
        log.info(FileUtils.readline(oldfile, file.getCharsetName(), 1));

        long l = FileUtils.equalsIgnoreLineSeparator(oldfile, file.getCharsetName(), txtfile, file.getCharsetName(), context.getReaderBuffer());
        Assert.assertEquals(0L, l);
    }
}
