package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;

import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.FtpList;
import cn.org.expect.util.ResourcesUtils;

/**
 * 封装脚本命令的运行逻辑
 */
public class PassiveCommand extends AbstractFileCommand {

    private boolean remote;

    /**
     * 初始化 PassiveCommand
     */
    public PassiveCommand(UniversalCommandCompiler compiler, String str, boolean remote) {
        super(compiler, str);
        this.remote = remote;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        OSFtpCommand ftp = FtpList.get(context).getFTPClient();
        if (ftp == null) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message031", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        boolean print = session.isEchoEnable() || forceStdout;
        if (print) {
            stdout.println("passive" + (this.remote ? " remote" : ""));
        }

        ftp.enterPassiveMode(this.remote);
        return 0;
    }
}
