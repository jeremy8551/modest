package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

/**
 * 使用 Idea 或 Eclipse 等 IDE 的 debug 模式的时候，方便打断点添加的命令（可以将断点打在这个命令中）
 */
public class DebugCommand extends AbstractCommand {

    public DebugCommand(UniversalCommandCompiler compiler, String script) {
        super(compiler, script);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println("debug");
        }
        return 0;
    }
}
