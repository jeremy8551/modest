package cn.org.expect.zip;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jeremy8551@qq.com
 * @createtime 2024/8/15
 */
public class TarTest {

    @Test
    public void test1() throws IOException {
        File file = Util.createfile("tar");
        DefaultEasyContext context = new DefaultEasyContext("sout+:info");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(TarCompress.class, compress.getClass());
    }

    @Test
    public void test2() throws IOException {
        File parent = FileUtils.createTempDirectory(null);
        File file = new File(parent, StringUtils.toRandomUUID() + ".tar"); // 文件所在目录
        FileUtils.createFile(file);

        File dir = FileUtils.createTempDirectory(null); // 文件所在目录
        FileUtils.createDirectory(dir);

        File f0 = new File(dir, "t1.txt");
        FileUtils.assertCreateFile(f0);
        FileUtils.write(f0, "utf-8", false, "f0");

        File f1 = new File(dir, "t2.txt");
        FileUtils.assertCreateFile(f1);

        TarCompress c = new TarCompress();
        c.setFile(file);
        c.archiveFile(f0, null, "utf-8");
        c.archiveFile(f1, null, "utf-8");
        c.close();
        System.out.println("压缩文件: " + file.getAbsolutePath());

        File untardir = FileUtils.createTempDirectory(null); // 文件所在目录
        Assert.assertTrue(FileUtils.createDirectory(untardir));
        System.out.println("解压目录: " + untardir.getAbsolutePath());
        c.extract(untardir.getAbsolutePath(), "UTF-8");
        c.close();

        File[] filelist = FileUtils.array(untardir.listFiles());
        for (File f : filelist) {
            System.out.println("解压后文件: " + f.getAbsolutePath());
            System.out.println(FileUtils.readline(f, "utf-8", 0));
        }
    }
}
