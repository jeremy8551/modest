package cn.org.expect.script.sample;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.test.annotation.RunWithLogSettings;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithLogSettings("sout:info")
@RunWithFeature("db2")
public class DB2Test {

    @Test
    public void test() {
        EasyContext context = new DefaultEasyContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.evaluate(". classpath:/script/sample/db2.sql");
    }
}
