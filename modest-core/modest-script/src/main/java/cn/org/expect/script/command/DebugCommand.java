package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

public class DebugCommand extends AbstractCommand {

    public DebugCommand(UniversalCommandCompiler compiler, String script) {
        super(compiler, script);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        stdout.println("debug");
        return 0;
    }

    public void terminate() throws Exception {
    }
}
