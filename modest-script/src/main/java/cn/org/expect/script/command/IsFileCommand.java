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
 * 确定远程服务器上是否存在指定文件<br>
 * 确定本地服务器上是否存在指定文件
 */
public class IsFileCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 文件绝对路径 */
    private String filepath;

    /** true表示本地 false表示远程服务器 */
    private boolean localhost;

    /** true表示布尔值取反 */
    private boolean reverse;

    public IsFileCommand(UniversalCommandCompiler compiler, String command, String filepath, boolean localhost, boolean reverse) {
        super(compiler, command);
        this.filepath = filepath;
        this.localhost = localhost;
        this.reverse = reverse;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "isfile", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        OSFtpCommand ftp = FtpList.get(context).getFTPClient();
        boolean print = session.isEchoEnable() || forceStdout;
        if (this.localhost || ftp == null) {
            File file = PathExpression.toFile(session, context, this.filepath);

            if (this.reverse) {
                if (print) {
                    stdout.println("!isfile " + file.getAbsolutePath());
                }

                return FileUtils.isFile(file) ? UniversalScriptCommand.COMMAND_ERROR : 0;
            } else {
                if (print) {
                    stdout.println("isfile " + file.getAbsolutePath());
                }

                return FileUtils.isFile(file) ? 0 : UniversalScriptCommand.COMMAND_ERROR;
            }
        } else {
            String filepath = PathExpression.resolve(session, context, this.filepath, false);

            if (this.reverse) {
                if (print) {
                    stdout.println("!isfile " + filepath);
                }

                return ftp.isFile(filepath) ? UniversalScriptCommand.COMMAND_ERROR : 0;
            } else {
                if (print) {
                    stdout.println("isfile " + filepath);
                }

                return ftp.isFile(filepath) ? 0 : UniversalScriptCommand.COMMAND_ERROR;
            }
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
