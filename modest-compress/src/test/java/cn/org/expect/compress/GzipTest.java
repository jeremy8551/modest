package cn.org.expect.compress;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
import cn.org.expect.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 Gzip 文件
 */
@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:info")
public class GzipTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test1() throws IOException {
        File file = FileUtils.createTempFile("CompressTestFile.txt.tar.gz");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(TarCompress.class, compress.getClass());
    }

    /**
     * 测试 tar.gz
     */
    @Test
    public void test2() throws IOException {
        File compressfile = FileUtils.createTempFile("t1.txt.gz");
        File file = new File(compressfile.getParentFile(), "t1.txt");

        String charsetName = "UTF-8";
        FileUtils.write(file, charsetName, false, file.getAbsolutePath());

        GzipCompress compress = new GzipCompress();
        compress.setFile(compressfile);
        compress.archiveFile(file, null, charsetName);
        compress.close();

        Assert.assertFalse(file.exists());

        // 解压缩
        compress.setFile(compressfile);
        compress.extract(compressfile.getParentFile(), charsetName);
        compress.close();

        Assert.assertTrue(file.exists());
    }
}
