package cn.org.expect.javax.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 演示当前模块的基础调用方式
 */
public class ScriptSample {

    /**
     * 启动示例或应用
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("usl");
        engine.eval("echo hello world!");
    }
}
