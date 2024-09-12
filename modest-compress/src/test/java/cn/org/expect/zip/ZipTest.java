package cn.org.expect.zip;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.DefaultEasyetlContext;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.Assert;
import org.junit.Test;

/**
 * 单元测试类
 *
 * @author jeremy8551@qq.com
 * @createtime 2024/8/15
 */
public class ZipTest {

    @Test
    public void test() throws IOException {
        File file = Util.createfile("zip");
        DefaultEasyetlContext context = new DefaultEasyetlContext("sout+");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(ZipCompress.class, compress.getClass());
    }

    /**
     * 测试 zip 格式的子目录压缩与删除压缩包中的文件
     *
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        String ext = "zip";

        DefaultEasyetlContext context = new DefaultEasyetlContext("sout+");
        File compressfile = Util.createfile(ext); // 压缩文件

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
        System.out.println("压缩完毕 ..");

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
        System.out.println("文件 " + compressfile.getAbsolutePath() + " 解压完毕!");
        System.out.println("目录: " + compressfile.getParentFile().getAbsolutePath());

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
        DefaultEasyetlContext context = new DefaultEasyetlContext("sout+");

        File compressfile = Util.createfile(ext); // 压缩文件

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
        System.out.println("压缩完毕 ..");

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
        System.out.println("文件 " + compressfile.getAbsolutePath() + " 解压完毕!");
        System.out.println("目录: " + compressfile.getParentFile().getAbsolutePath());

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
        DefaultEasyetlContext context = new DefaultEasyetlContext();

        File dir = FileUtils.createTempDirectory(null);
        File f0 = new File(dir, "t1.txt");
        File f1 = new File(dir, "t2.txt");
        File f2 = new File(dir, "t3.txt");
        File f3 = new File(dir, "t4.txt");

        FileUtils.write(f0, StringUtils.CHARSET, false, "中文字符chinese charactors");
        FileUtils.write(f1, StringUtils.CHARSET, false, "中文字符chinese charactors");
        FileUtils.write(f2, StringUtils.CHARSET, false, "中文字符chinese charactors");
        FileUtils.write(f3, StringUtils.CHARSET, false, "中文字符chinese charactors");

        File zipfile = FileUtils.createTempFile("com.zip");
        FileUtils.createFile(zipfile);
        Util.compress(context, dir, zipfile, StringUtils.CHARSET, false);

        Assert.assertTrue(f0.exists() && f1.exists() && f2.exists() && f3.exists() && dir.exists() && dir.isDirectory());

        Assert.assertTrue(FileUtils.delete(zipfile));
        Assert.assertTrue(FileUtils.createFile(zipfile));
        Util.compress(context, dir, zipfile, StringUtils.CHARSET, true);
        Assert.assertTrue(zipfile.exists() && !dir.exists());
    }

    /**
     * 测试解压文件
     */
    @Test
    public void test4() throws IOException {
        DefaultEasyetlContext context = new DefaultEasyetlContext("sout+");

        File zipfile = FileUtils.createTempFile("com.zip");
        FileUtils.createFile(zipfile);

        File dir = FileUtils.createTempDirectory(null);
        File f0 = new File(dir, "t1.txt");
        File f1 = new File(dir, "t2.txt");
        File f2 = new File(dir, "t3.txt");
        File f3 = new File(dir, "t4.txt");

        FileUtils.write(f0, StringUtils.CHARSET, false, "中文字符chinese charactors");
        FileUtils.write(f1, StringUtils.CHARSET, false, "中文字符chinese charactors");
        FileUtils.write(f2, StringUtils.CHARSET, false, "中文字符chinese charactors");
        FileUtils.write(f3, StringUtils.CHARSET, false, "中文字符chinese charactors");

        Util.compress(context, dir, zipfile, StringUtils.CHARSET, true);
        Assert.assertTrue(!f0.exists() && !f1.exists() && !f2.exists() && !f3.exists() && !dir.exists());

        Util.uncompress(context, zipfile, dir.getParentFile(), StringUtils.CHARSET, false);
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

        DefaultEasyetlContext context = new DefaultEasyetlContext("sout+");
        Compress c = context.getBean(Compress.class, FileUtils.getFilenameSuffix(file.getName()));
        c.setFile(file);
        c.archiveFile(f1, null);
        c.archiveFile(f2, null);
        c.close();

        ZipFile zipfile = new ZipFile(file, StringUtils.CHARSET) {
            public void close() throws IOException {
                throw new IOException();
            }
        };

        IO.closeQuietly(zipfile);
    }
}
