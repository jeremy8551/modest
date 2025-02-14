package cn.org.expect.zip;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 rar 文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/8/15
 */
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class RarTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test1() throws IOException {
        File file = CompressFileUtils.createfile("tar.rar");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(RarCompress.class, compress.getClass());
    }
}
