package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.os.OS;
import cn.org.expect.os.OSCommand;
import cn.org.expect.printer.OutputStreamPrinter;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.SSHClientMap;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 执行操作系统命令 <br>
 * os ps -ef
 */
public class ExecuteOSCommand extends AbstractTraceCommand implements UniversalScriptInputStream, JumpCommandSupported, NohupCommandSupported {

    /** 操作系统命令 */
    private String oscommand;

    /** 命令执行的终端 */
    private OSCommand terminal;

    public ExecuteOSCommand(UniversalCommandCompiler compiler, String command, String oscommand) {
        super(compiler, command);
        this.oscommand = oscommand;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.oscommand)) {
            this.oscommand = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "os", this.oscommand);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        this.terminal = SSHClientMap.get(context).last(); // 优先使用 declare ssh2 client 语句定义的客户端
        if (this.terminal == null) {
            OS os = context.getContainer().getBean(OS.class);
            this.terminal = os.getOSCommand();
        }

        UniversalScriptAnalysis analysis = session.getAnalysis();
        String command = analysis.replaceShellVariable(session, context, this.oscommand, true, false);
        String charsetName = StringUtils.coalesce(this.terminal.getCharsetName(), context.getCharsetName());
        return this.terminal.execute(analysis.unQuotation(command), 0, new OutputStreamPrinter(stdout, charsetName), new OutputStreamPrinter(stderr, charsetName));
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.terminal != null) {
            this.terminal.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enableJump() {
        return true;
    }
}
