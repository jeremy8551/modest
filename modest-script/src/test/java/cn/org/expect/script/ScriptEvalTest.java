package cn.org.expect.script;

import java.io.IOException;
import java.util.ArrayList;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 {@link UniversalScriptEngine#evaluate(String, Object...)} 返回值是否正确
 */
@EasyLog("sout+")
@RunWith(ModestRunner.class)
public class ScriptEvalTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test1() throws IOException {
        UniversalScriptEngineFactory factory = this.context.getBean(UniversalScriptEngineFactory.class);
        UniversalScriptEngine engine = factory.getScriptEngine();
        Object list = engine.evaluate("exit evalList", "evalList", new ArrayList<Object>());
        Assert.assertEquals(ArrayList.class, list.getClass());
    }

    @Test
    public void test2() throws IOException {
        UniversalScriptEngineFactory factory = this.context.getBean(UniversalScriptEngineFactory.class);
        UniversalScriptEngine engine = factory.getScriptEngine();
        try {
            Object evalList = engine.evaluate(". classpath:/script/testEvalValue.sql", "evalList", new ArrayList<Object>());
            Assert.assertEquals(ArrayList.class, evalList.getClass());
        } finally {
            engine.evaluate("exit 0");
        }
    }
}
