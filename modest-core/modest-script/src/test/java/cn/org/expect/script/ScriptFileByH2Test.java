package cn.org.expect.script;

import java.io.IOException;
import java.util.Properties;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 使用 H2 数据库进行测试
 */
@RunWith(ModestRunner.class)
public class ScriptFileByH2Test {

    @EasyBean
    private Properties properties;

    @Test
    public void test() throws IOException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        try {
            engine.setBindings(ScriptUtils.to(this.properties), UniversalScriptContext.ENVIRONMENT_SCOPE);
            engine.eval(". classpath:/script/testByH2.sql");
            Assert.fail();
        } catch (UniversalScriptException se) {
            se.printStackTrace(System.out);
            Assert.assertEquals("1000", engine.getContext().getAttribute("testvalue000"));
            Assert.assertEquals("-3", se.getMessage());
        } finally {
            engine.eval("exit 0");
        }
    }
}
