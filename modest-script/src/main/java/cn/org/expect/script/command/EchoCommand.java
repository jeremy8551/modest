package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.CallbackCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;

/**
 * 使用标准输出打印：结尾不带回车换行符的字符串
 */
public class EchoCommand extends AbstractTraceCommand implements CallbackCommandSupported, NohupCommandSupported {

    /** 输出内容 */
    protected String message;

    public EchoCommand(UniversalCommandCompiler compiler, String command, String message) {
        super(compiler, command);
        this.message = message;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            UniversalScriptAnalysis analysis = session.getAnalysis();
            String message = analysis.replaceShellVariable(session, context, this.message, true, true);
            stdout.print(analysis.unescapeString(analysis.unQuotation(message))); // 最后执行转义
        }
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }

    public String[] getArguments() {
        return new String[]{"echo", this.message};
    }
}
