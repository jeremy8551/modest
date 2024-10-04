package cn.org.expect.script;

import java.io.IOException;
import java.util.Properties;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.ModestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试数据装载命令
 */
@RunWith(ModestRunner.class)
public class ScriptEngineDBExportCmdTest {

    @EasyBean
    private Properties properties;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws ScriptException, IOException {
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        ScriptEngine engine = manager.getScriptEngine();
        engine.setBindings(ScriptUtils.to(this.properties), UniversalScriptContext.ENVIRONMENT_SCOPE);
        engine.eval(". classpath:/script/test_db_export.sql");
    }

}
