package cn.org.expect.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.org.expect.ModestException;
import org.junit.Assert;
import org.junit.Test;

public class ResourcesUtilsTest {

    /**
     * 测试外部资源文件中的属性
     */
    @Test
    public void test() throws IOException {
        InputStream in = ModestException.class.getResourceAsStream("script_res.properties");
        File tempFile = FileUtils.createTempFile("Messages.properties");
        FileUtils.write(tempFile, CharsetName.UTF_8, false, in);

        System.setProperty(ResourcesUtils.PROPERTY_RESOURCE, tempFile.getAbsolutePath()); // 设置外部资源文件
        ResourcesUtils.getRepository().load(ClassUtils.getClassLoader());

        Assert.assertEquals("filepath", ResourcesUtils.getMessage("script.engine.usage.msg888"));
        Assert.assertFalse(ResourcesUtils.existsMessage("test.msg.stdout"));
    }

    @Test
    public void test1() throws IOException {
        System.setProperty(ResourcesUtils.PROPERTY_LOCALE, "zh_CN"); // 设置外部资源文件
        ResourcesUtils.getRepository().load(ClassUtils.getClassLoader());
        Assert.assertEquals("中文", ResourcesUtils.getMessage("zh.standard.output.msg001"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("test", ResourcesUtils.getMessage("test.standard.output.msg001"));
    }
}
