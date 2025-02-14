package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.FileUtils;

public class Dos2UnixCommand extends AbstractTraceCommand implements JumpCommandSupported, NohupCommandSupported {

    private String value;

    public Dos2UnixCommand(UniversalCommandCompiler compiler, String command, String value) {
        super(compiler, command);
        this.value = value;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(analysis.replaceShellVariable(session, context, this.command, true, true));
        }

        String str = analysis.unQuotation(analysis.replaceShellVariable(session, context, this.value, true, true));
        if (FileUtils.isFile(str)) {
            FileUtils.dos2unix(new File(str), context.getCharsetName(), new File(session.getTempDir(), "dos2unix"));
        } else {
            if (session.isEchoEnable() || forceStdout) {
                stdout.println(FileUtils.replaceLineSeparator(str, FileUtils.LINE_SEPARATOR_UNIX));
            }
        }
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enableJump() {
        return true;
    }
}
