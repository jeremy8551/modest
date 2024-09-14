package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.FtpList;
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 从远程服务器上下载文件或目录 <br>
 * get remotefile localfile
 */
public class GetCommand extends AbstractFileCommand implements NohupCommandSupported {

    private OSFtpCommand ftp;

    private String localfile;

    private String remotefile;

    public GetCommand(UniversalCommandCompiler compiler, String command, String localfile, String remotefile) {
        super(compiler, command);
        this.localfile = localfile;
        this.remotefile = remotefile;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String localfilepath = FileUtils.replaceFolderSeparator(analysis.replaceShellVariable(session, context, this.localfile, true, true, true, false), true);
        String remotefilepath = FileUtils.replaceFolderSeparator(analysis.replaceShellVariable(session, context, this.remotefile, true, true, true, false), false);

        if (session.isEchoEnable() || forceStdout) {
            if (StringUtils.isBlank(localfilepath)) {
                stdout.println("get " + remotefilepath);
            } else {
                stdout.println("get " + remotefilepath + " " + localfilepath);
            }
        }

        try {
            this.ftp = FtpList.get(context).getFTPClient();
            if (this.ftp == null) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr035"));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            if (analysis.isBlankline(localfilepath)) {
                localfilepath = session.getDirectory();
            }

            File file = new ScriptFile(session, context, localfilepath);
            if (!file.exists()) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr037", localfilepath));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            this.ftp.download(remotefilepath, new ScriptFile(session, context, localfilepath));
            return 0;
        } finally {
            this.ftp = null;
        }
    }

    public void terminate() throws Exception {
        if (this.ftp != null) {
            this.ftp.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }

}
