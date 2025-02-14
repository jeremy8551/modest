package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.FtpList;

/**
 * 显示本地目录路径<br>
 * 显示远程目录路径<br>
 */
public class PwdCommand extends AbstractFileCommand implements NohupCommandSupported {

    /** true表示本地 false表示远程服务器 */
    private boolean localhost;

    public PwdCommand(UniversalCommandCompiler compiler, String command, boolean localhost) {
        super(compiler, command);
        this.localhost = localhost;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        OSFtpCommand ftp = FtpList.get(context).getFTPClient();
        if (this.localhost || ftp == null) {
            if (print) {
                String pwd = session.getDirectory();
                stdout.println(pwd);
                session.putValue(pwd);
            }
            return 0;
        } else {
            if (print) {
                String pwd = ftp.pwd();
                stdout.println(pwd);
                session.putValue(pwd);
            }
            return 0;
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
