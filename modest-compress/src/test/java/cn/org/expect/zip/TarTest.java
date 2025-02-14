package cn.org.expect.zip;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 Tar 文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/8/15
 */
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class TarTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test1() throws IOException {
        File file = CompressFileUtils.createfile("tar");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(TarCompress.class, compress.getClass());
    }

    @Test
    public void test2() throws IOException {
        File parent = FileUtils.createTempDirectory(null);
        File tarFile = new File(parent, StringUtils.toRandomUUID() + ".tar"); // 文件所在目录
        FileUtils.createFile(tarFile);

        File dir = FileUtils.createTempDirectory(null); // 文件所在目录
        FileUtils.createDirectory(dir);

        File f0 = new File(dir, "t1.txt");
        FileUtils.assertCreateFile(f0);
        FileUtils.write(f0, CharsetName.UTF_8, false, "f0");

        File f1 = new File(dir, "t2.txt");
        FileUtils.assertCreateFile(f1);

        TarCompress compress = new TarCompress();
        compress.setFile(tarFile);
        compress.archiveFile(f0, null, CharsetName.UTF_8);
        compress.archiveFile(f1, null, CharsetName.UTF_8);
        compress.close();

        // 文件所在目录
        File unTarDir = FileUtils.createTempDirectory(null);
        Assert.assertTrue(FileUtils.createDirectory(unTarDir));
        compress.extract(unTarDir.getAbsolutePath(), CharsetName.UTF_8);
        compress.close();

        File[] fileList = FileUtils.array(unTarDir.listFiles());
        for (File file : fileList) {
            if ("t1.txt".equals(file.getName())) {
                Assert.assertEquals("f0", FileUtils.readline(file, CharsetName.UTF_8, 0));
                continue;
            }

            if ("t2.txt".equals(file.getName())) {
                Assert.assertEquals("", FileUtils.readline(file, CharsetName.UTF_8, 0));
                continue;
            }
        }
    }
}
