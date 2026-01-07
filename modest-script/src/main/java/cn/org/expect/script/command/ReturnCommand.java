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
import cn.org.expect.util.StringUtils;

/**
 * 从用户自定义方法中退出
 */
public class ReturnCommand extends AbstractSlaveCommand implements UniversalScriptInputStream, LoopCommandKind {

    /** 返回值 */
    private String returnValue;

    public ReturnCommand(UniversalCommandCompiler compiler, String command, String returnValue) {
        super(compiler, command);
        this.returnValue = returnValue;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.returnValue)) {
            this.returnValue = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "return", this.returnValue);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (this.existsOwner()) {
            UniversalScriptAnalysis analysis = session.getAnalysis();
            String returnValue = analysis.replaceShellVariable(session, context, this.returnValue, true, true);
            String value = analysis.trim(returnValue, 0, 1);

            // 没有返回值
            if (value.length() == 0) {
                // stderr.println(ResourcesUtils.getMessage("script.stderr.message014", this.command));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            // 返回值
            if (StringUtils.isInt(value)) {
                // if (session.isEchoEnable() || forceStdout) {
                //    stdout.println("return " + value);
                //}
                return Integer.parseInt(value);
            }

            // stderr.println(ResourcesUtils.getMessage("script.stderr.message015", this.command, value));
            return UniversalScriptCommand.COMMAND_ERROR;
        } else {
            // stderr.println(ResourcesUtils.getMessage("script.stderr.message013", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public int kind() {
        return LoopCommandKind.RETURN_COMMAND;
    }
}
