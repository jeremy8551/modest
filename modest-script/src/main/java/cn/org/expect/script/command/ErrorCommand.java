package cn.org.expect.script.command;

import java.io.File;
import java.io.Reader;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.CallbackCommandSupported;
import cn.org.expect.util.IO;

/**
 * 通过脚本引擎错误信息输出流输出信息
 *
 * @author jeremy8551@gmail.com
 */
public class ErrorCommand extends AbstractTraceCommand implements UniversalScriptInputStream, CallbackCommandSupported {

    protected String message;

    public ErrorCommand(UniversalCommandCompiler compiler, String command, String message) {
        super(compiler, command);
        this.message = message;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws Exception {
        this.message = IO.read(in, new StringBuilder()).toString();
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        stderr.println(analysis.replaceShellVariable(session, context, analysis.unQuotation(this.message), true, !analysis.containsQuotation(this.message)));
        return 0;
    }

    public String[] getArguments() {
        return new String[]{"error", this.message};
    }
}
