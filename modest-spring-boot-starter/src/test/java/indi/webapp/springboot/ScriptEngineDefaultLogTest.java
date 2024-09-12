package indi.webapp.springboot;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试使用标准日志输出脚本引擎日志
 */
public class ScriptEngineDefaultLogTest {

    @Test
    public void test() {
        ScriptEngineManager e = new ScriptEngineManager();
        ScriptEngine engine;
        try {
            engine = e.getEngineByExtension("etl");
            engine.eval("echo 输出日志到 file://${temp}/" + ScriptEngineDefaultLogTest.class.getSimpleName() + ".log");
            engine.eval("help > ${temp}/" + ScriptEngineDefaultLogTest.class.getSimpleName() + ".log");
            engine.eval("exit 0");
        } catch (Exception e1) {
            e1.printStackTrace();
            Assert.fail();
        }
    }
}