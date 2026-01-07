package cn.org.expect.script;

import java.io.IOException;
import java.util.Properties;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试数据装载命令
 */
@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:info")
public class ScriptFileExportTest {

    @EasyBean
    private Properties properties;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        engine.getContext().getEnvironmentVariable().putAll(this.properties);
        engine.evaluate(". classpath:/script/test_db_export.sql");
    }
}
