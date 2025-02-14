package cn.org.expect.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import org.junit.Assert;
import org.junit.Test;

public class BufferLineReaderTest {

    @Test
    public void test() throws IOException {
        File file = FileUtils.createTempFile(this.getClass().getSimpleName() + ".txt");
        FileUtils.write(file, "UTF-8", false, "1\r2\n3\r\n4\n");
        BufferedReader in = IO.getBufferedReader(file, "UTF-8");
        Assert.assertEquals("1", in.readLine());
        Assert.assertEquals("2", in.readLine());
        Assert.assertEquals("3", in.readLine());
        Assert.assertEquals("4", in.readLine());
        in.close();
    }
}
