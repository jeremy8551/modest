package cn.org.expect.zip;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.DefaultEasyetlContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * 单元测试类
 *
 * @author jeremy8551@qq.com
 * @createtime 2024/8/15
 */
public class RarTest {

    @Test
    public void test1() throws IOException {
        File file = Util.createfile("tar.rar");
        DefaultEasyetlContext context = new DefaultEasyetlContext("sout+info");
        Compress compress = context.getBean(Compress.class, file);
        Assert.assertEquals(RarCompress.class, compress.getClass());
    }
}
