package cn.org.expect.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import org.junit.Assert;
import org.junit.Test;

public class BufferedLineWriterTest {

    @Test
    public void test() throws IOException {
        File file = FileUtils.createTempFile("testfile.txt");

        BufferedWriter out = new BufferedWriter(file, "UTF-8", false, 2);
        out.write("0");
        out.writeLine("1");
        out.writeLine("2", "\n");
        out.writeLine("3", "\n");
        out.writeLine("4", "\n");
        out.writeLine("5", "\n");
        out.flush();
        out.close();

        Assert.assertEquals(5, out.getLineNumber());
        Assert.assertEquals("\n", out.getLineSeparator());
        Assert.assertEquals("UTF-8", out.getCharsetName());

        Assert.assertEquals("01", FileUtils.readline(file, "UTF-8", 1));
        Assert.assertEquals("3", FileUtils.readline(file, "UTF-8", 3));
        Assert.assertEquals("5", FileUtils.readline(file, "UTF-8", 5));
    }

    @Test
    public void test1() throws IOException {
        File file = FileUtils.createTempFile("testfile.txt");

        OutputStreamWriter writer = IO.getFileWriter(file, "UTF-8", false);
        BufferedWriter out = new BufferedWriter(writer, 2);
        out.write("0");
        out.writeLine("1");
        out.writeLine("2", "\n");
        out.writeLine("3", "\n");
        out.writeLine("4", "\n");
        out.writeLine("5", "\n");
        out.flush();
        out.close();

        Assert.assertEquals(5, out.getLineNumber());
        Assert.assertEquals("\n", out.getLineSeparator());
        Assert.assertNull(out.getCharsetName());

        Assert.assertEquals("01", FileUtils.readline(file, "UTF-8", 1));
        Assert.assertEquals("3", FileUtils.readline(file, "UTF-8", 3));
        Assert.assertEquals("5", FileUtils.readline(file, "UTF-8", 5));
    }

    @Test
    public void test2() throws IOException {
        File file = FileUtils.createTempFile("testfile.txt");

        BufferedWriter out = new BufferedWriter(file, "UTF-8", 2);
        out.write("0");
        out.writeLine("1");
        out.writeLine("2", "\n");
        out.writeLine("3", "\n");
        out.writeLine("4", "\n");
        out.writeLine("5", "\n");
        out.flush();
        out.close();

        Assert.assertEquals(5, out.getLineNumber());
        Assert.assertEquals("\n", out.getLineSeparator());
        Assert.assertEquals("UTF-8", out.getCharsetName());

        Assert.assertEquals("01", FileUtils.readline(file, "UTF-8", 1));
        Assert.assertEquals("3", FileUtils.readline(file, "UTF-8", 3));
        Assert.assertEquals("5", FileUtils.readline(file, "UTF-8", 5));
    }

    @Test
    public void test3() throws IOException {
        File file = FileUtils.createTempFile("testfile.txt");

        BufferedWriter out = new BufferedWriter(file, "UTF-8");
        out.write("0");
        out.writeLine("1");
        out.writeLine("2", "\n");
        out.writeLine("3", "\n");
        out.writeLine("4", "\n");
        out.writeLine("5", "\n");
        out.flush();
        out.close();

        Assert.assertEquals(5, out.getLineNumber());
        Assert.assertEquals("\n", out.getLineSeparator());
        Assert.assertEquals("UTF-8", out.getCharsetName());

        Assert.assertEquals("01", FileUtils.readline(file, "UTF-8", 1));
        Assert.assertEquals("3", FileUtils.readline(file, "UTF-8", 3));
        Assert.assertEquals("5", FileUtils.readline(file, "UTF-8", 5));
    }

    @Test
    public void test0() throws IOException {
        try {
            File file = FileUtils.createTempFile("testfile.txt");
            OutputStreamWriter writer = IO.getFileWriter(file, "UTF-8", false);
            new BufferedWriter(writer, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("0", e.getMessage());
        }

        try {
            BufferedWriter out = new BufferedWriter(null, 0);
            out.close();
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
    }
}
