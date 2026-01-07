package cn.org.expect.compress;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
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
@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:info")
public class ZipTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        File file = new File(FileUtils.changeFilenameExt(FileUtils.createTempFile(null).getAbsolutePath(), "zip"));
        Compress compress = this.context.getBean(Compress.class, file);
        Assert.assertEquals(ZipCompress.class, compress.getClass());
    }

    /**
     * 测试 zip 格式的子目录压缩与删除压缩包中的文件
     */
    @Test
    public void test1() throws IOException {
        File dir = FileUtils.createTempDirectory(null);
        File compressfile = new File(dir, "TestFile.zip");

        File f1 = new File(compressfile.getParentFile(), "t1.txt");
        File f2 = new File(compressfile.getParentFile(), "t2.txt");
        File f3 = new File(compressfile.getParentFile(), "t3.txt");
        File f4 = new File(compressfile.getParentFile(), "t4中文.txt");
        File f5 = new File(compressfile.getParentFile(), "level1");
        File f51 = new File(f5, "level21.txt");
        File f52 = new File(f5, "level22.txt");

        String ext = "zip";
        String charsetName = "utf-8";
        FileUtils.write(f1, charsetName, false, ext + " " + f1.getAbsolutePath());
        FileUtils.write(f2, charsetName, false, ext + " " + f2.getAbsolutePath());
        FileUtils.write(f3, charsetName, false, ext + " " + f3.getAbsolutePath());
        FileUtils.write(f4, charsetName, false, ext + " 测试中文 " + f4.getAbsolutePath());
        FileUtils.createDirectory(f5);
        FileUtils.write(f51, charsetName, false, ext + " 测试中文 " + f51.getAbsolutePath());
        FileUtils.write(f52, charsetName, false, ext + " 测试中文 " + f52.getAbsolutePath());

        ZipCompress compress = new ZipCompress();
        compress.setFile(compressfile);
        compress.setMobileMode(true);
        compress.setVerbose(true);
        compress.setRecursion(true);
        compress.archiveFile(f1, null, charsetName);
        compress.archiveFile(f2, "", charsetName);
        compress.archiveFile(f3, "dir", charsetName);
        compress.archiveFile(f4, "dir/cdir", charsetName);
        compress.archiveFile(f5, null, charsetName);
        compress.close();

        Assert.assertFalse(f1.exists());
        Assert.assertFalse(f2.exists());
        Assert.assertFalse(f3.exists());
        Assert.assertFalse(f4.exists());
        Assert.assertFalse(f5.exists());
        Assert.assertFalse(f51.exists());
        Assert.assertFalse(f52.exists());

        // 解压到 TestFile 目录
        compress = new ZipCompress();
        compress.setFile(compressfile);
        File outputDir = new File(compressfile.getParentFile(), FileUtils.getFilenameNoSuffix(compressfile.getName()));
        compress.extract(outputDir, charsetName);
        compress.close();

        // 删除 zip 中某个 Entry
        compress = new ZipCompress();
        compress.setFile(compressfile);
        compress.removeEntry(charsetName, "dir/cdir");
        compress.close();

        // 解压缩
        compress.setFile(compressfile);
        compress.extract(compressfile.getParentFile(), charsetName);
        compress.close();

        Assert.assertTrue(f1.exists());
        Assert.assertTrue(f2.exists());
        Assert.assertTrue(new File(f3.getParentFile(), "dir/" + f3.getName()).exists());
        Assert.assertFalse(new File(f4.getParentFile(), "dir/cdir/").exists());
        Assert.assertTrue(f5.exists());
        Assert.assertTrue(f51.exists());
        Assert.assertTrue(f52.exists());
    }

    @Test
    public void test2() throws IOException {
        File compressFile = FileUtils.createTempFile(StringUtils.toRandomUUID() + ".zip");
        ZipCompress compress = new ZipCompress();
        compress.setFile(compressFile);
        compress.setVerbose(true);
        compress.setRecursion(true);
        CompressUtils.test(compress);
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

        Compress compress = new ZipCompress();
        compress.setFile(file);
        compress.archiveFile(f1, null);
        compress.archiveFile(f2, null);
        compress.close();

        ZipFile zipfile = new ZipFile(file, CharsetUtils.get()) {
            public void close() throws IOException {
                throw new IOException();
            }
        };

        IO.closeQuietly(zipfile);
    }
}
