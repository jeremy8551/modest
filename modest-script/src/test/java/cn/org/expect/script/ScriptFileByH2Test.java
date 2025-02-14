package cn.org.expect.script;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 使用 H2 数据库进行测试
 */
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class ScriptFileByH2Test {
    private final static Log log = LogFactory.getLog(ScriptFileByH2Test.class);

    @EasyBean
    private Properties properties;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        engine.getContext().getEnvironmentVariable().putAll(this.properties);
        engine.getContext().getEnvironmentVariable().put("curr_dir_path", new File(ClassUtils.getClasspath(ScriptFileByH2Test.class), "script").getAbsolutePath());
        engine.getContext().getEnvironmentVariable().put("temp", FileUtils.getTempDir(this.getClass().getSimpleName()).getAbsolutePath());
        try {
            engine.evaluate(". classpath:/script/testByH2.sql");
            Assert.fail();
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            Assert.assertEquals("1000", engine.getContext().getVariable("testvalue000"));
            Assert.assertTrue(StringUtils.inArray("-3", StringUtils.splitByBlank(e.getMessage())));
        }
    }
}
