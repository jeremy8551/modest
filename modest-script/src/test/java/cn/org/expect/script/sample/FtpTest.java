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
@RunWithFeature("ftp")
public class FtpTest {

    @Test
    public void test() {
        EasyContext context = new DefaultEasyContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.evaluate(". classpath:/script/sample/ftp.sql");
    }
}
