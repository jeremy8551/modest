package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;

/**
 * 命令替换功能
 *
 * @author jeremy8551@gmail.com
 */
public class SubCommand extends AbstractCommand implements NohupCommandSupported {

    /** 命令替换语句 */
    private String script;

    public SubCommand(UniversalCommandCompiler compiler, String command, String script) {
        super(compiler, command);
        this.script = script;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(this.command);
        }

        return context.getEngine().evaluate(session, context, stdout, stderr, this.script);
    }

    public boolean enableNohup() {
        return true;
    }
}
