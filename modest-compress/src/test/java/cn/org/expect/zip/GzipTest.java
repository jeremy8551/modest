package cn.org.expect.zip;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 Gzip 文件
 */
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class GzipTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        File file = CompressFileUtils.createfile("txt.tar.gz");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(GzipCompress.class, compress.getClass());
    }

    /**
     * 测试 tar.gz
     */
    @Test
    public void test1() throws IOException {
        String ext = "gz";
        File compressfile = CompressFileUtils.createfile(ext); // 压缩文件

        File f1 = new File(compressfile.getParentFile(), "t1.txt");
        File f2 = new File(compressfile.getParentFile(), "t2.txt");
        File f3 = new File(compressfile.getParentFile(), "t3.txt");
        File f4 = new File(compressfile.getParentFile(), "t4中文.txt");

        // 先删除
        f1.delete();
        f2.delete();
        f3.delete();
        f4.delete();

        FileUtils.write(f1, "utf-8", false, ext + " " + f1.getAbsolutePath());
        FileUtils.write(f2, "utf-8", false, ext + " " + f2.getAbsolutePath());
        FileUtils.write(f3, "utf-8", false, ext + " " + f3.getAbsolutePath());
        FileUtils.write(f4, "utf-8", false, ext + " 测试中文 " + f4.getAbsolutePath());

        Compress c = context.getBean(Compress.class, compressfile);
        c.setFile(compressfile);
        c.archiveFile(f1, null, "utf-8");
        c.archiveFile(f2, "", "utf-8");
        c.archiveFile(f3, "dir", "utf-8");
        c.archiveFile(f4, "dir/cdir", "utf-8");
        c.close();

        File parentdir = new File(f4.getParentFile(), "dir/cdir");
        f1.delete();
        f2.delete();
        f3.delete();
        f4.delete();
        Assert.assertTrue(FileUtils.deleteDirectory(parentdir));

        Assert.assertFalse(f1.exists());
        Assert.assertFalse(f2.exists());
        Assert.assertFalse(f3.exists());
        Assert.assertFalse(f4.exists());

        // 解压缩
        c.setFile(compressfile);
        c.extract(compressfile.getParentFile(), "UTF-8");
        c.close();
    }

    @Test
    public void test3() throws IOException {
        File dir = FileUtils.createTempDirectory(null); // 文件所在目录
        Assert.assertTrue(FileUtils.createDirectory(dir));

        File f0 = new File(dir, "t1.txt");
        FileUtils.assertCreateFile(f0);
        FileUtils.write(f0, "utf-8", false, "f0");

        File f1 = new File(dir, "t2.txt");
        FileUtils.assertCreateFile(f1);

        File f2 = new File(dir, "t3.txt");
        FileUtils.assertCreateFile(f2);

        File f3 = new File(dir, "t4.txt");
        FileUtils.assertCreateFile(f3);

        File dir1 = FileUtils.createTempDirectory(null); // 文件压缩后存储的目录
        FileUtils.createDirectory(dir1);

        GzipCompress c = new GzipCompress();
        c.gzipDir(dir, dir1, true, true);
        c.close();

        c.gunzipDir(dir1, dir1, true, true);
        c.close();

        File[] files = FileUtils.array(dir1.listFiles());
        for (File file : files) {
            if ("t1.txt".equals(file.getName())) {
                Assert.assertEquals("f0", FileUtils.readline(file, CharsetName.UTF_8, 0));
                continue;
            }
            if ("t2.txt".equals(file.getName())) {
                Assert.assertEquals("", FileUtils.readline(file, CharsetName.UTF_8, 0));
                continue;
            }
            if ("t3.txt".equals(file.getName())) {
                Assert.assertEquals("", FileUtils.readline(file, CharsetName.UTF_8, 0));
                continue;
            }
            if ("t4.txt".equals(file.getName())) {
                Assert.assertEquals("", FileUtils.readline(file, CharsetName.UTF_8, 0));
                continue;
            }
        }
    }
}
