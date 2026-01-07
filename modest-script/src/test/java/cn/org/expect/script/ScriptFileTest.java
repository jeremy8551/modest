package cn.org.expect.script;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:debug")
public class ScriptFileTest {

    @EasyBean
    private Properties properties;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.getContext().getEnvironmentVariable().putAll(this.properties);
        engine.getContext().getEnvironmentVariable().put("curr_dir_path", new File(ClassUtils.getClasspath(ScriptFileTest.class), "script").getAbsolutePath());
        engine.getContext().getEnvironmentVariable().put("temp", FileUtils.getTempDir(this.getClass().getSimpleName()).getAbsolutePath());
        try {
            engine.evaluate(". classpath:/script/testNoDB.sql param1 'param2' \"12 \"");
            Assert.fail();
        } catch (UniversalScriptException e) { // 为了测试发生错误时，提示信息是否正确
            if (StringUtils.inArray("77", StringUtils.splitByBlank(e.getMessage())) && "1000".equals(engine.getContext().getVariable("testvalue000"))) {
                Assert.assertTrue(true);
            } else {
                e.printStackTrace();
                Assert.fail();
            }
        } finally {
            engine.evaluate("exit 0");
        }
    }
}
