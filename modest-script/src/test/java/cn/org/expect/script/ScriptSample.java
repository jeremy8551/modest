package cn.org.expect.script;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;

/**
 * 演示当前模块的基础调用方式
 */
public class ScriptSample {

    /**
     * 启动示例或应用
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        EasyContext context = new DefaultEasyContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.evaluate("echo hello world!");
    }
}
