package cn.org.expect.script.command;

import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

public class JavaCommandTest1 extends AbstractJavaCommand {

    public JavaCommandTest1() {
        super();
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String[] args) throws Exception {
        context.getEngine().evaluate("step \"Nested statements in a Java class\"");
        return 0;
    }

    public boolean enableJump() {
        return false;
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enablePipe() {
        return false;
    }
}
