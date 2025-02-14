package cn.org.expect.sort;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.io.BufferedWriter;
import cn.org.expect.io.CommonTextTableFile;
import cn.org.expect.io.CommonTextTableFileReaderListener;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class TextTableFileTest {
    private final static Log log = LogFactory.getLog(TextTableFileTest.class);

    @EasyBean
    private EasyContext context;

    @Test
    public void testAll() throws IOException {
        CommonTextTableFile file = new CommonTextTableFile();
        file.setDelimiter(",");
        file.setCharsetName(CharsetUtils.get());

        File testFile = this.getTestFile(file);
        file.setAbsolutePath(testFile.getAbsolutePath());

        TextTableFileReader in = file.getReader(IO.FILE_BYTES_BUFFER_SIZE);
        try {
            Assert.assertEquals(file.getCharsetName(), CharsetUtils.get());
            Assert.assertEquals(21, file.getColumn());
            Assert.assertFalse(file.existsEscape());
            Assert.assertEquals(",", file.getDelimiter());

            int no = 0;
            TextTableLine line;
            while ((line = in.readLine()) != null) {
                Assert.assertEquals(++no, in.getLineNumber());
                Assert.assertEquals(line.getColumn(), file.getColumn());
                String firstfield = StringUtils.trimBlank(line.getColumn(1));
                Assert.assertTrue(Integer.parseInt(firstfield) >= 1 && Integer.parseInt(firstfield) <= 50000);
                Assert.assertTrue(StringUtils.inArray(in.getLineSeparator(), "\n", "\r", "\r\n"));
                Assert.assertTrue(StringUtils.inArray(in.getLineSeparator(), "\n", "\r", "\r\n"));
            }
            Assert.assertEquals(50000, in.getLineNumber());

            // 读取第一行
            Assert.assertNotNull((line = in.readLine(1)));
            Assert.assertEquals("50000", StringUtils.trimBlank(line.getColumn(1)));
            Assert.assertEquals(1, in.getLineNumber());

            // 读取最后一行
            Assert.assertNotNull((line = in.readLine(50000)));
            Assert.assertEquals("1", StringUtils.trimBlank(line.getColumn(1)));
            Assert.assertEquals(50000, in.getLineNumber());

            // 读取指定行
            Assert.assertNotNull((line = in.readLine(10000)));
            Assert.assertEquals("40001", StringUtils.trimBlank(line.getColumn(1)));
            Assert.assertEquals(10000, in.getLineNumber());

            // 回到首行之前即刚刚打开文件的状态
            Assert.assertNull(in.readLine(0));
            Assert.assertEquals(0, in.getLineNumber());
        } finally {
            in.close();
        }
    }

    @Test
    public void testMergeLine() throws IOException {
        File parent = FileUtils.getTempDir("test", TextTableFileTest.class.getSimpleName());
        File dir = new File(parent, Dates.format08(new Date()));
        FileUtils.assertCreateDirectory(dir);

        File file = new File(dir, "SortTableFileTestMergeLine" + Dates.format17() + ".txt");
        FileWriter fw = new FileWriter(file);
        fw.write("1,11,12,13,14" + "\r\n");
        fw.write("2,2\r1,22,23,24" + "\r\n");
        fw.write("3,31,3\n2,33,34" + "\r\n");
        fw.write("4,4\r1,4\r\n2,4\n3,44" + "\r\n");
        fw.close();

        CommonTextTableFile tablefile = new CommonTextTableFile();
        tablefile.setAbsolutePath(file.getAbsolutePath());
        tablefile.setCharsetName("UTF-8");
        tablefile.setDelimiter(",");

        TextTableFileReader in = tablefile.getReader(IO.FILE_BYTES_BUFFER_SIZE);
        in.setListener(new CommonTextTableFileReaderListener());
        int i = 1;
        TextTableLine line;
        while ((line = in.readLine()) != null) {
            if (i == in.getLineNumber() && i != Integer.parseInt(StringUtils.trimBlank(line.getColumn(1)))) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(1)));
            }

            if (in.getLineNumber() == 2 && !line.getColumn(2).equals("21")) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(2)));
            }

            if (in.getLineNumber() == 3 && !line.getColumn(3).equals("32")) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(3)));
            }

            if (in.getLineNumber() == 4 && !line.getColumn(2).equals("41")) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(2)));
            }

            if (in.getLineNumber() == 4 && !line.getColumn(3).equals("42")) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(3)));
            }

            if (in.getLineNumber() == 4 && !line.getColumn(4).equals("43")) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(4)));
            }

            i++;
        }
        in.close();
    }

    @Test
    public void testMergeLine1() throws IOException {
        File parent = FileUtils.getTempDir("test", TextTableFileTest.class.getSimpleName());
        File dir = new File(parent, Dates.format08(new Date()));
        FileUtils.assertCreateDirectory(dir);
        File file = new File(dir, "SortTableFileTestMergeLine" + Dates.format17() + ".txt");
        FileWriter writer = new FileWriter(file);
        writer.write("1,11,12,13,14" + "\r\n");
        writer.write("2,2\r1,22,23,24" + "\r\n");
        writer.write("3,31,3\n2,33,34" + "\r\n");
        writer.write("4,4\r1,4\r\n2,4\n3,44" + "\r\n");
        writer.close();

        CommonTextTableFile tablefile = new CommonTextTableFile();
        tablefile.setAbsolutePath(file.getAbsolutePath());
        tablefile.setCharsetName("UTF-8");
        tablefile.setDelimiter(",");
        TextTableFileReader in = tablefile.getReader(IO.FILE_BYTES_BUFFER_SIZE);
        in.setListener(new CommonTextTableFileReaderListener() {
            public void processLineSeparator(TextTableFile file, TextTableLine line, long lineNumber) {
            }
        });

        int i = 1;
        TextTableLine line;
        while ((line = in.readLine()) != null) {
            if (i == in.getLineNumber() && i != Integer.parseInt(StringUtils.trimBlank(line.getColumn(1)))) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(1)));
            }

            if (in.getLineNumber() == 2 && !line.getContent().equals("2,2\r1,22,23,24")) {
                Assert.fail("no: " + i + ", line: " + in.getLineNumber() + ", str: " + StringUtils.escapeLineSeparator(line.getContent()));
            }

            if (in.getLineNumber() == 3 && !line.getContent().equals("3,31,3\n2,33,34")) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(3)));
            }

            if (in.getLineNumber() == 4 && !line.getContent().equals("4,4\r1,4\r\n2,4\n3,44")) {
                Assert.fail(i + ", " + in.getLineNumber() + ", " + StringUtils.trimBlank(line.getColumn(2)));
            }

            i++;
        }
        in.close();
    }

    @Test
    public void testSort() throws Exception {
        CommonTextTableFile file = new CommonTextTableFile();
        file.setDelimiter(",");
        file.setCharsetName(CharsetUtils.get());

        TableFileSortContext cxt = new TableFileSortContext();
        cxt.setWriterBuffer(50);
        cxt.setMaxRows(10000);
        cxt.setDeleteFile(true);
        cxt.setThreadNumber(1);
        cxt.setFileCount(2);
        cxt.setReaderBuffer(8192);
        cxt.setThreadSource(context.getBean(ThreadSource.class));
        cxt.setDuplicate(false);

        TableFileSorter sorter = new TableFileSorter(cxt);

        File f0 = this.getTestFile(file);
        file.setAbsolutePath(f0.getAbsolutePath());

        cxt.setThreadNumber(1);
        cxt.setFileCount(2);
        File rs = sorter.execute(context, file, "1");
        TextTableFile cf = file.clone();
        cf.setAbsolutePath(rs.getAbsolutePath());
        this.checkFile(cf);

        this.getTestFile(file);
        cxt.setThreadNumber(2);
        cxt.setFileCount(2);
        rs = sorter.execute(context, file, "1");
        cf = file.clone();
        cf.setAbsolutePath(rs.getAbsolutePath());
        this.checkFile(cf);

        this.getTestFile(file);
        cxt.setThreadNumber(5);
        cxt.setFileCount(2);
        rs = sorter.execute(context, file, "1");
        cf = file.clone();
        cf.setAbsolutePath(rs.getAbsolutePath());
        this.checkFile(cf);

        this.getTestFile(file);
        cxt.setThreadNumber(3);
        cxt.setFileCount(3);
        rs = sorter.execute(context, file, "1");

        cf = file.clone();
        cf.setAbsolutePath(rs.getAbsolutePath());
        this.checkFile(cf);

        this.getTestFile(file);
        rs = sorter.execute(context, file, "1");
        cf = file.clone();
        cf.setAbsolutePath(rs.getAbsolutePath());
        this.checkFile(cf);
    }

    /**
     * 测试倒序排序
     */
    @Test
    public void test1() throws Exception {
        CommonTextTableFile file = new CommonTextTableFile();
        file.setDelimiter(",");
        file.setCharsetName(CharsetUtils.get());

        File f = this.getTestFile(file);
        file.setAbsolutePath(f.getAbsolutePath());

        TableFileSortContext cxt = new TableFileSortContext();
        cxt.setWriterBuffer(50);
        cxt.setMaxRows(10000);
        cxt.setDeleteFile(true);
        cxt.setThreadNumber(3);
        cxt.setFileCount(3);
        cxt.setReaderBuffer(8192);
        cxt.setKeepSource(false);
        cxt.setThreadSource(context.getBean(ThreadSource.class));
        cxt.setDuplicate(false);

        TableFileSorter sorter = new TableFileSorter(cxt);
        sorter.execute(context, file, "1 desc");
        int i = 50000;
        TextTableFileReader in = file.getReader(IO.FILE_BYTES_BUFFER_SIZE);
        TextTableLine line;
        while ((line = in.readLine()) != null) {
            if (i != Integer.parseInt(StringUtils.trimBlank(line.getColumn(1)))) {
                Assert.fail(i + " != " + Integer.parseInt(StringUtils.trimBlank(line.getColumn(1))));
            }

            if (i + 19 != Integer.parseInt(StringUtils.trimBlank(line.getColumn(20)))) {
                Assert.fail((i + 19) + " != " + Integer.parseInt(StringUtils.trimBlank(line.getColumn(20))));
            }

            i--;
        }
        in.close();

        sorter.execute(context, file, "1 asc");
        this.checkFile(file);

        sorter.execute(context, file, "int(1) asc");
        this.checkFile(file);
    }

    /**
     * 测试从指定位置开始读取文件
     */
    @Test
    public void test2() throws IOException {
        CommonTextTableFile file = new CommonTextTableFile();
        file.setDelimiter(",");
        file.setCharsetName(CharsetUtils.get());

        File f = this.getTestFile(file);
        file.setAbsolutePath(f.getAbsolutePath());
        long chars = 0;
        int count = 1000;

        // 继续向下写入数据
        int total = 40000;
        BufferedWriter out = new BufferedWriter(f, "UTF-8", 20);
        for (int i = total, z = 0; i > 0; i--) {
            StringBuilder buf = new StringBuilder();
            for (int j = 0; j < 20; j++) {
                buf.append(StringUtils.right(i + j, 8, ' '));
                buf.append(file.getDelimiter());
            }

            if (++z <= count) {
                chars += buf.length() + String.valueOf(Settings.LINE_SEPARATOR).length();
            }

            if (out.writeLine(buf.toString(), String.valueOf(Settings.LINE_SEPARATOR))) {
                out.flush();
            }
        }
        out.close();

        // 向下读取数据文件判断行数是否相等
        TextTableFileReader in = file.getReader(IO.FILE_BYTES_BUFFER_SIZE);
        Assert.assertTrue("越过文件 " + f.getAbsolutePath() + " 失败! rows: " + count + ", chars: " + chars, in.skip(chars, count));
        try {
            int c = 0;
            while (in.readLine() != null) {
                c++;
            }
            Assert.assertEquals(c + count, total);
        } finally {
            in.close();
        }
    }

    @Test
    public void test3() throws IOException {
        CommonTextTableFile file = new CommonTextTableFile();
        file.setDelimiter(",");
        file.setCharsetName(CharsetUtils.get());
        File f = this.getTestFile(file);
        file.setAbsolutePath(f.getAbsolutePath());

        int stat = 181;
        int max = 819;
        TextTableFileReader in = file.getReader(stat, max, 100);
        try {
            log.info("起始起始位置: {}", in.getStartPointer());
            int count = 0;
            TextTableLine line;
            while ((line = in.readLine()) != null) {
                int lineSize = StringUtils.length(line.getContent(), file.getCharsetName()) + StringUtils.length(line.getLineSeparator(), file.getCharsetName());
                log.info("line: [{}] 实际 {} 个字节长度!", line.getContent(), lineSize);
                count += lineSize;
                if (in.getLineNumber() >= 200) {
                    break;
                }
            }
            Assert.assertEquals(181, in.getStartPointer());
            Assert.assertEquals(count, 905);
            log.info("最多能读取 {} 个字节! 实际读取个 {} 字节!", max, count);
        } finally {
            in.close();
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

    protected File getTestFile(TextTableFile file) throws IOException {
        File parent = FileUtils.getTempDir(TextTableFileTest.class.getSimpleName());
        File dir = new File(parent, Dates.format08(new Date()));
        FileUtils.assertCreateDirectory(dir);
        File f0 = new File(dir, "SortTableFile" + Dates.format17() + StringUtils.toRandomUUID() + ".txt");

        FileUtils.delete(f0);
        FileUtils.createFile(f0);

        FileWriter out = new FileWriter(f0);
        for (int i = 50000; i > 0; i--) {
            StringBuilder buf = new StringBuilder();
            for (int j = 0; j < 20; j++) {
                buf.append(StringUtils.right(i + j, 8, ' '));
                buf.append(file.getDelimiter());
            }
            out.write(buf + String.valueOf(Settings.LINE_SEPARATOR));

            if (i % 20 == 0) {
                out.flush();
            }
        }
        out.flush();
        out.close();
        return f0;
    }

    @Test
    public void testGetTextTableFileColumn() throws IOException {
        CommonTextTableFile txt = new CommonTextTableFile();
        File file = FileUtils.createTempFile("testfile.txt");

        FileUtils.write(file, CharsetUtils.get(), false, "1\n2");
        txt.setAbsolutePath(file.getAbsolutePath());
        txt.setDelimiter(",");
        Assert.assertEquals(txt.countColumn() + " == " + 1, txt.countColumn(), 1);

        FileUtils.write(file, CharsetUtils.get(), false, "");
        txt.setAbsolutePath(file.getAbsolutePath());
        txt.setDelimiter(",");
        Assert.assertEquals(txt.countColumn() + " == " + 0, txt.countColumn(), 0);

        FileUtils.write(file, CharsetUtils.get(), false, "1,2,3");
        txt.setAbsolutePath(file.getAbsolutePath());
        txt.setDelimiter(",");
        int c = txt.countColumn();
        Assert.assertEquals(c + " == " + 3, c, 3);
    }

    @Test
    public void testCloneTableFile() {
        CommonTextTableFile c = new CommonTextTableFile();
        c.setAbsolutePath("/home/user/shell/test.del");
        c.setCharsetName(CharsetUtils.get());
        c.setColumn(20);
        c.setDelimiter(",");

        TextTableFile t = c.clone();
        Ensure.isTrue(t.getAbsolutePath().equals(c.getAbsolutePath()) //
            && t.getCharsetName().equals(c.getCharsetName()) //
            && t.getColumn() == c.getColumn() //
            && t.getDelimiter().equals(c.getDelimiter()) //
        );
    }

    @Test
    public void testDeleteFileTableFile() throws IOException {
        File file = FileUtils.createTempFile("TestFile.txt");
        FileUtils.write(file, CharsetUtils.get(), false, "1,2,3\na,b,c"); // 写入表格行数据文件内容

        TextTableFile tf = new CommonTextTableFile();
        String path = file.getAbsolutePath();
        tf.setAbsolutePath(path);
        tf.setDelimiter(",");
        tf.setCharsetName(CharsetUtils.get());
        tf.delete();

        Assert.assertFalse(file.exists());
    }
}
