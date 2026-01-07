package cn.org.expect.compress;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
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
@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:info")
public class TarTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test1() throws IOException {
        File file = FileUtils.createTempFile("TestFile.tar");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(TarCompress.class, compress.getClass());
    }

    @Test
    public void test2() throws IOException {
        File parent = FileUtils.createTempDirectory(null);
        File compressFile = new File(parent, StringUtils.toRandomUUID() + ".tar"); // 文件所在目录
        FileUtils.createFile(compressFile);

        Compress compress = this.context.getBean(Compress.class, "tar");
        compress.setFile(compressFile);
        CompressUtils.test(compress);
    }
}
