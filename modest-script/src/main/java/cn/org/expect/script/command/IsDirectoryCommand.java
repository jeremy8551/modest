package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.FtpList;
import cn.org.expect.script.io.PathExpression;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 用于判断文件是否是目录
 */
public class IsDirectoryCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 文件绝对路径 */
    private String filepath;

    /** true表示本地 false表示远程服务器 */
    private boolean localhost;

    /** true表示布尔值取反 */
    private boolean reverse;

    public IsDirectoryCommand(UniversalCommandCompiler compiler, String command, String filepath, boolean localhost, boolean reverse) {
        super(compiler, command);
        this.filepath = filepath;
        this.localhost = localhost;
        this.reverse = reverse;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "isDirectory", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        OSFtpCommand ftp = FtpList.get(context).getFTPClient();
        boolean print = session.isEchoEnable() || forceStdout;
        if (this.localhost || ftp == null) {
            File file = PathExpression.toFile(session, context, this.filepath);

            if (this.reverse) {
                if (print) {
                    stdout.println("!isDirectory " + file.getAbsolutePath());
                }
                return FileUtils.isDirectory(file) ? UniversalScriptCommand.COMMAND_ERROR : 0;
            } else {
                if (print) {
                    stdout.println("isDirectory " + file.getAbsolutePath());
                }
                return FileUtils.isDirectory(file) ? 0 : UniversalScriptCommand.COMMAND_ERROR;
            }
        } else {
            String filepath = PathExpression.resolve(session, context, this.filepath, false);
            if (this.reverse) {
                if (print) {
                    stdout.println("!isDirectory " + filepath);
                }
                return ftp.isDirectory(filepath) ? UniversalScriptCommand.COMMAND_ERROR : 0;
            } else {
                if (print) {
                    stdout.println("isDirectory " + filepath);
                }
                return ftp.isDirectory(filepath) ? 0 : UniversalScriptCommand.COMMAND_ERROR;
            }
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
