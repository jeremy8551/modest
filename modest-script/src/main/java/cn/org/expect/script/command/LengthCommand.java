package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;

import cn.org.expect.expression.DataUnitExpression;
import cn.org.expect.os.OSFile;
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
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 用于测试本地文件或远程文件的大小，或测量字符串的长度 <br>
 * <br>
 * length string; <br>
 * length -h string; <br>
 * length -b string; <br>
 * length -c string; <br>
 * length -f filepath; <br>
 * length -r remotefilepath; <br>
 */
public class LengthCommand extends AbstractTraceCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 操作类型 */
    private char type;

    /** 字符串或文件路径 */
    private String parameter;

    public LengthCommand(UniversalCommandCompiler compiler, String command, char type, String parameter) {
        super(compiler, command);
        this.type = type;
        this.parameter = parameter;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.parameter)) {
            this.parameter = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "length", this.parameter);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        UniversalScriptAnalysis analysis = session.getAnalysis();
        if (this.type == 'r') { // remote file path
            OSFtpCommand ftp = FtpList.get(context).getFTPClient();
            if (ftp == null) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message031", this.command));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            String filepath = ScriptFile.replaceFilepath(session, context, this.parameter, false);
            if (ftp.isDirectory(filepath)) {
                String parent = FileUtils.getParent(filepath);
                String filename = FileUtils.getFilename(filepath);
                List<OSFile> list = ftp.ls(parent);
                for (OSFile file : list) {
                    if (file.getName().equals(filename)) {
                        if (print) {
                            stdout.println(file.length());
                        }
                        return 0;
                    }
                }

                stderr.println(ResourcesUtils.getMessage("script.stderr.message044", this.command, filepath));
                return UniversalScriptCommand.COMMAND_ERROR;
            } else if (ftp.isFile(filepath)) { // expression is remote file path
                OSFile file = Ensure.notNull(CollectionUtils.first(ftp.ls(filepath)));
                if (print) {
                    stdout.println(file.length());
                }
                return 0;
            } else {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message044", this.command, filepath));
                return UniversalScriptCommand.COMMAND_ERROR;
            }
        } else if (this.type == 'f') { // local file path
            File file = new ScriptFile(session, context, this.parameter);
            if (print) {
                stdout.println(file.length());
            }
            return 0;
        } else if (this.type == 'c') { // character length
            String str = analysis.unQuotation(analysis.replaceShellVariable(session, context, this.parameter, true, true));
            if (print) {
                stdout.println(str.length());
            }
            return 0;
        } else if (this.type == 'b') { // string's bytes
            String str = analysis.unQuotation(analysis.replaceShellVariable(session, context, this.parameter, true, true));
            if (print) {
                stdout.println(StringUtils.toBytes(str, context.getCharsetName()).length);
            }
            return 0;
        } else if (this.type == 'h') { // Humanize view
            String str = analysis.unQuotation(analysis.replaceShellVariable(session, context, this.parameter, true, true));
            if (print) {
                stdout.println(DataUnitExpression.toString(BigDecimal.valueOf(StringUtils.toBytes(str, context.getCharsetName()).length)));
            }
            return 0;
        } else {
            stderr.println(this.command);
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
