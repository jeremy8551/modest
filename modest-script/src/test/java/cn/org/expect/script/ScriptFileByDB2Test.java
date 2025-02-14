package cn.org.expect.script;

import java.io.IOException;
import java.util.Properties;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class ScriptFileByDB2Test {

    @EasyBean
    private Properties properties;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        try {
            engine.getContext().getEnvironmentVariable().putAll(this.properties);
            engine.evaluate(". classpath:/script/test.sql");
            Assert.fail();
        } catch (UniversalScriptException se) {
            Assert.assertEquals("1000", engine.getContext().getVariable("testvalue000"));
            Assert.assertEquals("333", se.getMessage());
        } finally {
            engine.evaluate("exit 0");
        }
    }
}
