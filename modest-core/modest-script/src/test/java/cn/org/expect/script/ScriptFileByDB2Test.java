package cn.org.expect.script;

import java.io.IOException;
import java.util.Properties;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(ModestRunner.class)
public class ScriptFileByDB2Test {

    @EasyBean
    private Properties properties;

    @Test
    public void test() throws IOException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        try {
            engine.setBindings(ScriptUtils.to(this.properties), UniversalScriptContext.ENVIRONMENT_SCOPE);
            engine.eval(". classpath:/script/test.sql");
            Assert.fail();
        } catch (ScriptException se) {
            Assert.assertEquals("1000", engine.getContext().getAttribute("testvalue000"));
            Assert.assertEquals("333", StringUtils.splitByBlank(se.getMessage())[1]);
        } finally {
            engine.eval("exit 0");
        }
    }
}