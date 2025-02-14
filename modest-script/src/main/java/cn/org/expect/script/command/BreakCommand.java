package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.util.ResourcesUtils;

public class BreakCommand extends AbstractSlaveCommand implements LoopCommandKind {

    public final static int KIND = 40;

    public BreakCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (this.existsOwner()) {
            return 0;
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message001"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public int kind() {
        return BreakCommand.KIND;
    }
}
