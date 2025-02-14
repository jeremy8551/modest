package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.FtpList;
import cn.org.expect.util.ResourcesUtils;

public class ByeCommand extends AbstractTraceCommand {

    public ByeCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println("bye");
        }

        OSFtpCommand ftp = FtpList.get(context).getFTPClient();
        if (ftp == null) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message031", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        } else {
            FtpList.get(context).close();
            return 0;
        }
    }
}
