package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

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
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import cn.org.expect.zip.Compress;

public class GunzipCommand extends AbstractFileCommand implements UniversalScriptInputStream, JumpCommandSupported, NohupCommandSupported {

    /** 文件绝对路径 */
    private String filepath;

    private Compress compress;

    public GunzipCommand(UniversalCommandCompiler compiler, String command, String filepath) {
        super(compiler, command);
        this.filepath = filepath;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "gunzip", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        ScriptFile file = new ScriptFile(session, context, this.filepath);
        if (session.isEchoEnable() || forceStdout) {
            stdout.println("gunzip " + file.getAbsolutePath());
        }

        this.compress = context.getContainer().getBean(Compress.class, "gz");
        try {
            this.compress.setFile(file);
            this.compress.extract(file.getParentFile(), Settings.getFileEncoding());

            session.putValue(file);
            return this.compress.isTerminate() ? UniversalScriptCommand.TERMINATE : 0;
        } finally {
            this.compress.close();
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.compress != null) {
            this.compress.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enableJump() {
        return true;
    }
}
