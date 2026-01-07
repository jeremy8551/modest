package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.Settings;

public class StacktraceCommand extends AbstractTraceCommand implements NohupCommandSupported {

    private boolean printScript;

    private boolean printLineNumber;

    public StacktraceCommand(UniversalCommandCompiler compiler, String command, boolean printScript, boolean printLineNumber) {
        super(compiler, command);
        this.printScript = printScript;
        this.printLineNumber = printLineNumber;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            UniversalScriptException exp = session.getSystemVariable(UniversalScriptVariable.SYSTEM_LASTEXCEPTION);
            if (exp != null) {
                StringBuilder buf = new StringBuilder();

                // 添加发生错误的脚本语句
                if (this.printScript) {
                    buf.append(exp.getScript()).append(Settings.getLineSeparator());
                }

                // 添加行号
                if (this.printLineNumber) {
                    buf.append(exp.getLineNumber()).append(Settings.getLineSeparator());
                }

                // 格式化异常错误信息
                buf.append(context.getEngine().getFormatter().format(exp.getCause()));

                // 打印异常信息
                stdout.println(buf.toString());
            }
        }
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
