package cn.org.expect.script.command;

import cn.org.expect.printer.Printer;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

public class JavaCommandTest2 extends AbstractJavaCommand {

    public JavaCommandTest2() {
        super();
    }

    public void echoUsage(Printer out) {
    }

    @Override
    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String[] args) throws Exception {
        throw new Exception("测试脚本引擎处理异常错误!");
    }

    @Override
    public boolean enableJump() {
        return false;
    }

    @Override
    public boolean enableNohup() {
        return true;
    }

    @Override
    public boolean enablePipe() {
        return false;
    }
}
