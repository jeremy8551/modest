package cn.org.expect.script;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
import cn.org.expect.test.annotation.RunWithProperties;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 使用 H2 数据库进行测试
 */
@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:info")
@RunWithProperties(filename = "h2")
public class ScriptFileByH2Test {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        try {
            engine.evaluate(". classpath:/script/testByH2.sql", "curr_dir_path", new File(ClassUtils.getClasspath(ScriptFileByH2Test.class), "script").getAbsolutePath());
            Assert.fail();
        } catch (Exception e) {
            if (StringUtils.inArray("-3", StringUtils.splitByBlank(e.getMessage())) && "1000".equals(engine.getContext().getVariable("testvalue000"))) {
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
