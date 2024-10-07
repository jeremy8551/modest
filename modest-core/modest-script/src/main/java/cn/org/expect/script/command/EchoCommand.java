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
import cn.org.expect.util.StringUtils;

/**
 * 使用标准输出打印：结尾不带回车换行符的字符串
 */
public class EchoCommand extends AbstractTraceCommand implements CallbackCommandSupported, NohupCommandSupported {

    /** 输出内容 */
    protected String message;

    public EchoCommand(UniversalCommandCompiler compiler, String command, String message) {
        super(compiler, command);
        if (StringUtils.containsQuotation(message) != -1) {
            message = message.substring(1, message.length() - 1);
        }
        this.message = StringUtils.unescape(message);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            UniversalScriptAnalysis analysis = session.getAnalysis();
            String message = analysis.replaceShellVariable(session, context, this.message, true, true, true, true);
            stdout.print(message);
        }
        return 0;
    }

    public void terminate() throws Exception {
    }

    public boolean enableNohup() {
        return true;
    }

    public String[] getArguments() {
        return new String[]{"echo", this.message};
    }
}
