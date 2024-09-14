package cn.org.expect.script;

import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.org.expect.ioc.DefaultEasyetlContext;
import org.junit.Rule;
import org.junit.Test;

/**
 * 测试数据装载命令
 */
public class ScriptEngineDBLoadCmdTest {

    @Rule
    public WithDBRule rule = new WithDBRule();

    @Test
    public void test() throws ScriptException, IOException {
        // System.setProperty("cn.org.expect.dblog", "true");
        DefaultEasyetlContext context = rule.getContext();
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(context);
        ScriptEngine engine = manager.getScriptEngine();
        engine.setBindings(rule.getEnvironment(), UniversalScriptContext.ENVIRONMENT_SCOPE);
        engine.eval(". classpath:/script/test_db_load.sql");
    }

}
