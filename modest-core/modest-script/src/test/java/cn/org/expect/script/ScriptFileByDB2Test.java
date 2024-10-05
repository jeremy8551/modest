package cn.org.expect.script;

import java.io.IOException;
import java.util.Properties;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.ModestRunner;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
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
            engine.eval(". classpath:/script/test.sql");
            Assert.fail();
        } catch (UniversalScriptException se) {
            Assert.assertEquals("1000", engine.getContext().getAttribute("testvalue000"));
            Assert.assertEquals("333", se.getMessage());
        } finally {
            engine.eval("exit 0");
        }
    }
}
