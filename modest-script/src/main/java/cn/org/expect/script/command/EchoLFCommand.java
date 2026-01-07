package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

/**
 * 使用标准输出打印：结尾带回车换行符的字符串
 */
public class EchoLFCommand extends EchoCommand {

    public EchoLFCommand(UniversalCommandCompiler compiler, String command, String message) {
        super(compiler, command, message);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            UniversalScriptAnalysis analysis = session.getAnalysis();
            stdout.println(analysis.unescapeString(analysis.replaceShellVariable(session, context, analysis.unQuotation(this.message), true, !analysis.containsQuotation(this.message)))); // 最后执行转义
        }
        return 0;
    }
}
