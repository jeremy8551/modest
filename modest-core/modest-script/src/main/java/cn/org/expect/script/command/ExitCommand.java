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

    /** 脚本返回值 */
    private String exitcode;

    public ExitCommand(UniversalCommandCompiler compiler, String command, String exitcode) {
        super(compiler, command);
        this.exitcode = exitcode;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlankline(this.exitcode)) {
            this.exitcode = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr014", this.command, "exit", this.exitcode));
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        session.getCompiler().terminate();

        if (this.exitcode.length() == 0) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr011"));
            return UniversalScriptCommand.COMMAND_ERROR;
        } else if (StringUtils.isInt(this.exitcode)) {
            // 根节点的脚本引擎需要释放资源
            if (context.getParent() == null) {
                if (session.isEchoEnable() || forceStdout) {
                    stdout.println(ResourcesUtils.getMessage("script.message.stdout999"));
                }

                context.getEngine().close();
            } else {
                if (session.isEchoEnable() || forceStdout) {
                    stdout.println("exit " + this.exitcode);
                }
            }

            int value = Integer.parseInt(this.exitcode);
            session.removeValue();
            session.putValue("exit", value);
            return value;
        } else {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr012"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public void terminate() throws Exception {
    }

    public int kind() {
        return ExitCommand.KIND;
    }

}
