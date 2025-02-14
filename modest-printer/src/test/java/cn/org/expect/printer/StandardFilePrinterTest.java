package cn.org.expect.printer;

import java.io.File;
import java.io.IOException;

import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class StandardFilePrinterTest {

    /**
     * 使用指定用户名创建一个文件
     *
     * @return 文件
     */
    public File createfile() throws IOException {
        File dir = FileUtils.getTempDir("test", StandardFilePrinter.class.getSimpleName());
        FileUtils.createDirectory(dir);
        File file = new File(dir, StandardFilePrinter.class.getSimpleName() + StringUtils.toRandomUUID() + ".log");
        FileUtils.createFile(file);
        return file;
    }

    @Test
    public void test() throws IOException {
        File logfile = this.createfile();
        String charsetName = CharsetUtils.get();
        StandardFilePrinter out = new StandardFilePrinter(logfile, charsetName, false);
        out.println("1");
        out.println("2");
        out.println("3");
        out.close();

        Assert.assertEquals("1", FileUtils.readline(logfile, charsetName, 1));
        Assert.assertEquals("2", FileUtils.readline(logfile, charsetName, 2));
        Assert.assertEquals("3", FileUtils.readline(logfile, charsetName, 3));
    }
}
