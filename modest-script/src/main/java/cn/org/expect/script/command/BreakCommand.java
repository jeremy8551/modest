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

/**
 * 封装脚本命令的运行逻辑
 */
public class BreakCommand extends AbstractSlaveCommand implements LoopCommandKind {

    /**
     * 初始化 BreakCommand
     */
    public BreakCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    /** {@inheritDoc} */
    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (this.existsOwner()) {
            return 0;
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message001"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    /** {@inheritDoc} */
    public int kind() {
        return LoopCommandKind.BREAK_COMMAND;
    }
}
