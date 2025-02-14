package indi.webapp.springboot;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;

/**
 * 测试使用标准日志输出脚本引擎日志
 */
public class ScriptEngineDefaultLogTest {

    @Test
    public void test() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        engine.eval("echo 输出日志到 file://${temp}/Help.log");
        engine.eval("help > ${temp}/Help.log");
        engine.eval("exit 0");
    }
}
