package cn.org.expect.zip;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 zip 文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/8/15
 */
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class ZipTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        File file = CompressFileUtils.createfile("zip");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(ZipCompress.class, compress.getClass());
    }

    /**
     * 测试 zip 格式的子目录压缩与删除压缩包中的文件
     */
    @Test
    public void test1() throws IOException {
        String ext = "zip";
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

        File parentfile = new File(f4.getParentFile(), "dir/cdir/");
        Assert.assertTrue(f1.delete());
        Assert.assertTrue(f2.delete());
        Assert.assertTrue(f3.delete());
        Assert.assertTrue(f4.delete());
        Assert.assertTrue(FileUtils.deleteDirectory(parentfile));

        Assert.assertFalse(f1.exists());
        Assert.assertFalse(f2.exists());
        Assert.assertFalse(f3.exists());
        Assert.assertFalse(f4.exists());
        Assert.assertFalse(parentfile.exists());

        c = context.getBean(Compress.class, compressfile);
        c.setFile(compressfile);
        c.removeEntry("utf-8", "dir/cdir");
        c.close();

        // 解压缩
        c.setFile(compressfile);
        c.extract(compressfile.getParentFile(), "UTF-8");
        c.close();

        Assert.assertTrue(f1.exists());
        Assert.assertTrue(f2.exists());
        Assert.assertTrue(new File(f3.getParentFile(), "dir/" + f3.getName()).exists());
        Assert.assertFalse(parentfile.exists());
    }

    @Test
    public void test2() throws IOException {
        testcompress("zip");
    }

    private void testcompress(String ext) throws IOException {
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
        Assert.assertTrue(f1.delete());
        Assert.assertTrue(f2.delete());
        Assert.assertTrue(f3.delete());
        Assert.assertTrue(f4.delete());
        Assert.assertTrue(FileUtils.deleteDirectory(parentdir));

        Assert.assertFalse(f1.exists());
        Assert.assertFalse(f2.exists());
        Assert.assertFalse(f3.exists());
        Assert.assertFalse(f4.exists());

        // 解压缩
        c.setFile(compressfile);
        c.extract(compressfile.getParentFile(), "UTF-8");
        c.close();

        Assert.assertTrue(f1.exists());
        Assert.assertTrue(f2.exists());
        Assert.assertTrue(new File(f3.getParentFile(), "dir/" + f3.getName()).exists());

        File uncomf4 = new File(parentdir, f4.getName());
        Assert.assertTrue(uncomf4.exists());
    }

    /**
     * 测试压缩文件
     */
    @Test
    public void test3() throws IOException {
        File dir = FileUtils.createTempDirectory(null);
        File f0 = new File(dir, "t1.txt");
        File f1 = new File(dir, "t2.txt");
        File f2 = new File(dir, "t3.txt");
        File f3 = new File(dir, "t4.txt");

        FileUtils.write(f0, CharsetUtils.get(), false, "中文字符chinese charactors");
        FileUtils.write(f1, CharsetUtils.get(), false, "中文字符chinese charactors");
        FileUtils.write(f2, CharsetUtils.get(), false, "中文字符chinese charactors");
        FileUtils.write(f3, CharsetUtils.get(), false, "中文字符chinese charactors");

        File zipfile = FileUtils.createTempFile("com.zip");
        FileUtils.createFile(zipfile);
        CompressFileUtils.compress(context, dir, zipfile, CharsetUtils.get(), false);

        Assert.assertTrue(f0.exists() && f1.exists() && f2.exists() && f3.exists() && dir.exists() && dir.isDirectory());

        Assert.assertTrue(FileUtils.delete(zipfile));
        Assert.assertTrue(FileUtils.createFile(zipfile));
        CompressFileUtils.compress(context, dir, zipfile, CharsetUtils.get(), true);
        Assert.assertTrue(zipfile.exists() && !dir.exists());
    }

    /**
     * 测试解压文件
     */
    @Test
    public void test4() throws IOException {
        File zipfile = FileUtils.createTempFile("com.zip");
        FileUtils.createFile(zipfile);

        File dir = FileUtils.createTempDirectory(null);
        File f0 = new File(dir, "t1.txt");
        File f1 = new File(dir, "t2.txt");
        File f2 = new File(dir, "t3.txt");
        File f3 = new File(dir, "t4.txt");

        FileUtils.write(f0, CharsetUtils.get(), false, "中文字符chinese charactors");
        FileUtils.write(f1, CharsetUtils.get(), false, "中文字符chinese charactors");
        FileUtils.write(f2, CharsetUtils.get(), false, "中文字符chinese charactors");
        FileUtils.write(f3, CharsetUtils.get(), false, "中文字符chinese charactors");

        CompressFileUtils.compress(context, dir, zipfile, CharsetUtils.get(), true);
        Assert.assertTrue(!f0.exists() && !f1.exists() && !f2.exists() && !f3.exists() && !dir.exists());

        CompressFileUtils.uncompress(context, zipfile, dir.getParentFile(), CharsetUtils.get(), false);
        Assert.assertTrue(f0.exists() && f1.exists() && f2.exists() && f3.exists() && dir.exists() && dir.isDirectory());
    }

    @Test
    public void testCloseQuietlyZipFile() throws IOException {
        File file1 = FileUtils.createTempFile(null);
        File file = new File(FileUtils.changeFilenameExt(file1.getAbsolutePath(), "zip"));
        FileUtils.delete(file);
        FileUtils.createFile(file);

        File f1 = FileUtils.createTempFile(null);
        FileUtils.createFile(f1);

        File f2 = FileUtils.createTempFile(null);
        FileUtils.createFile(f2);

        Compress c = context.getBean(Compress.class, FileUtils.getFilenameSuffix(file.getName()));
        c.setFile(file);
        c.archiveFile(f1, null);
        c.archiveFile(f2, null);
        c.close();

        ZipFile zipfile = new ZipFile(file, CharsetUtils.get()) {
            public void close() throws IOException {
                throw new IOException();
            }
        };

        IO.closeQuietly(zipfile);
    }
}
