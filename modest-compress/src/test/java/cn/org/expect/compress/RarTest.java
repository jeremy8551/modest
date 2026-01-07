package cn.org.expect.compress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 rar 文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/8/15
 */
@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:info")
public class RarTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test1() throws IOException {
        File dir = FileUtils.createTempDirectory(null);

        String filename = "rartest.rar";
        File rartest = new File(dir, filename);
        IO.write(RarTest.class.getResourceAsStream(filename), new FileOutputStream(rartest), null);

        File outputDir = new File(rartest.getParentFile(), FileUtils.getFilenameNoSuffix(filename));
        FileUtils.createDirectory(outputDir);

        Compress compress = this.context.getBean(Compress.class, rartest);
        Assert.assertEquals(RarCompress.class, compress.getClass());
        compress.setFile(rartest);
        compress.extract(outputDir, CharsetName.UTF_8);
        compress.close();

        File testDir = CompressUtils.createTestDir();
        Assert.assertTrue(CompressUtils.equals(testDir, outputDir));
    }
}
