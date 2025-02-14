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
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

public class CdCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    private String filepath;

    private final boolean localhost;

    public CdCommand(UniversalCommandCompiler compiler, String command, String filepath, boolean localhost) {
        super(compiler, command);
        this.filepath = filepath;
        this.localhost = localhost;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "cd", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        OSFtpCommand ftp = FtpList.get(context).getFTPClient();
        if (this.localhost || ftp == null) {
            ScriptFile file = new ScriptFile(session, context, this.filepath);
            if (print) {
                stdout.println("cd " + file.getAbsolutePath());
            }
            session.setDirectory(file);
            session.putValue(file);
            return 0;
        } else {
            String filepath = ScriptFile.replaceFilepath(session, context, this.filepath, false);
            if (print) {
                stdout.println("cd " + filepath);
            }
            return ftp.cd(filepath) ? 0 : UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
