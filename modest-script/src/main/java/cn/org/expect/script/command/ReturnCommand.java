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
 * 从用户自定义方法中退出
 */
public class ReturnCommand extends AbstractSlaveCommand implements UniversalScriptInputStream, LoopCommandKind {

    public final static int KIND = 20;

    /** 返回值 */
    private String returnCode;

    public ReturnCommand(UniversalCommandCompiler compiler, String command, String returnCode) {
        super(compiler, command);
        this.returnCode = returnCode;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlankline(this.returnCode)) {
            this.returnCode = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr014", this.command, "return", this.returnCode));
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (this.existsOwner()) {
            UniversalScriptAnalysis analysis = session.getAnalysis();
            String value = analysis.trim(analysis.replaceVariable(session, context, this.returnCode, false), 0, 1);
            if (value.length() == 0) { // 如果没有返回值默认使用最近一次命名是否执行成功作为退出值
                stderr.println(ResourcesUtils.getMessage("script.message.stderr016"));
                return UniversalScriptCommand.COMMAND_ERROR;
            } else if (StringUtils.isInt(value)) {
                if (session.isEchoEnable() || forceStdout) {
                    stdout.println("return " + value);
                }
                return Integer.parseInt(value);
            } else {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr017", value));
                return UniversalScriptCommand.COMMAND_ERROR;
            }
        } else {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr015"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public void terminate() throws Exception {
    }

    public int kind() {
        return ReturnCommand.KIND;
    }

}
