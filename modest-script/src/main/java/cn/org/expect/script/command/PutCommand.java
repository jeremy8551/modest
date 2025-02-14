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
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.FtpList;
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 上传本地文件或目录到远程服务器 <br>
 * put localfile remotefile
 */
public class PutCommand extends AbstractFileCommand implements JumpCommandSupported, NohupCommandSupported {

    private OSFtpCommand ftp;

    /** 本地文件绝对路径 */
    private String localfile;

    /** 上传远程服务器的文件或目录 */
    private String remotefile;

    public PutCommand(UniversalCommandCompiler compiler, String command, String localfile, String remotefile) {
        super(compiler, command);
        this.localfile = localfile;
        this.remotefile = remotefile;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String localfilepath = ScriptFile.replaceFilepath(session, context, this.localfile, true);
        String remotedir = ScriptFile.replaceFilepath(session, context, this.remotefile, false);

        if (session.isEchoEnable() || forceStdout) {
            if (StringUtils.isBlank(remotedir)) {
                stdout.println("put " + localfilepath);
            } else {
                stdout.println("put " + localfilepath + " " + remotedir);
            }
        }

        try {
            this.ftp = FtpList.get(context).getFTPClient();
            if (this.ftp == null) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message031", this.command));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            File localfile = new ScriptFile(session, context, localfilepath);
            if (!localfile.exists()) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message032", this.command, localfilepath));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            if (analysis.isBlank(remotedir)) {
                remotedir = this.ftp.pwd();
            }

            this.ftp.upload(localfile, remotedir);
            return 0;
        } finally {
            this.ftp = null;
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.ftp != null) {
            this.ftp.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enableJump() {
        return true;
    }
}
