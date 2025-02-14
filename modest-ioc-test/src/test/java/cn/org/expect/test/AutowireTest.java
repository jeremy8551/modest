package cn.org.expect.test;

import java.util.Properties;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@EasyLog("sout+:debug")
@RunWith(ModestRunner.class)
public class AutowireTest extends SuperClass2 {
    private final static Log log = LogFactory.getLog(AutowireTest.class);

    @EasyBean
    Properties properties;

    @EasyBean("${modest.log.size}")
    private int size;

    @EasyBean
    protected String name;

    @Test
    public void test() {
        log.info("properties: {}", StringUtils.toString(this.properties));
        Assert.assertEquals(10, size);
        Assert.assertEquals(1100, super.longVal);
        Assert.assertEquals("info", super.log);
        Assert.assertEquals("test", name);
        Assert.assertEquals('e', super.charVal);
        Assert.assertTrue(super.booleanVal);
    }
}
