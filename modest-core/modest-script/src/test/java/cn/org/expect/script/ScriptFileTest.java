package cn.org.expect.script;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.console.ConsoleLogBuilder;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
public class ScriptFileTest {

    @EasyBean
    private Properties properties;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        File logfile = FileUtils.createTempFile("UniversalScriptEngine.log");
        System.out.println("脚本引擎输出日志, file://" + logfile.getAbsolutePath());
        FileUtils.clearFile(logfile);

        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        engine.getContext().getEnvironmentVariable().putAll(this.properties);
        engine.getContext().getEnvironmentVariable().put("curr_dir_path", FileUtils.joinPath(ClassUtils.getClasspath(ScriptFileTest.class), "script"));
        engine.getContext().getEnvironmentVariable().put("temp", FileUtils.getTempDir("test", "script").getAbsolutePath());
        try {
            engine.eval(". classpath:/script/testNoDB.sql ");
            Assert.fail();
        } catch (UniversalScriptException se) {
            se.printStackTrace(System.out);
            Assert.assertEquals("1000", engine.getContext().getAttribute("testvalue000"));
            Assert.assertEquals("333", se.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            engine.eval("exit 0");
        }
    }
}
