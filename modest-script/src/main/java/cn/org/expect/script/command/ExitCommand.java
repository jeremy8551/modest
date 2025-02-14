package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 退出脚本引擎
 */
public class ExitCommand extends AbstractTraceCommand implements UniversalScriptInputStream, LoopCommandKind {

    public final static int KIND = 10;

    /** 返回值 */
    private String exitValue;

    public ExitCommand(UniversalCommandCompiler compiler, String command, String exitValue) {
        super(compiler, command);
        this.exitValue = exitValue;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.exitValue)) {
            this.exitValue = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "exit", this.exitValue);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        session.getCompiler().terminate();
        String exitValue = session.getAnalysis().replaceShellVariable(session, context, this.exitValue, true, false);

        if (session.isEchoEnable() || forceStdout) {
            stdout.println("exit " + exitValue);
        }

        // 根节点的脚本引擎需要释放资源
        if (context.getParent() == null) {
            context.getEngine().close();
        }

        // 空指针
        if ("null".equalsIgnoreCase(exitValue)) {
            session.putValue(null);
            return 0;
        }

        // 返回值
        if (StringUtils.isInt(exitValue)) {
            int value = Integer.parseInt(exitValue);
            session.putValue(value);
            return value;
        }

        // 返回变量
        if (context.containsVariable(exitValue)) {
            Object variable = context.getVariable(exitValue);
            session.putValue(variable);
            return 0;
        }

        // 空白
        if (StringUtils.isBlank(exitValue)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message009", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        // 非法
        stderr.println(ResourcesUtils.getMessage("script.stderr.message010", this.command, exitValue));
        return UniversalScriptCommand.COMMAND_ERROR;
    }

    public int kind() {
        return ExitCommand.KIND;
    }
}
