package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.compress.ZipCompress;
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
import cn.org.expect.script.io.PathExpression;
import cn.org.expect.script.io.ScriptOutputWriter;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

public class UnzipCommand extends AbstractFileCommand implements UniversalScriptInputStream, JumpCommandSupported, NohupCommandSupported {

    /** 文件绝对路径 */
    private String filepath;

    /** 指定解压到哪个目录 */
    private String outputDir;

    /** Zip压缩 */
    private volatile ZipCompress compress;

    public UnzipCommand(UniversalCommandCompiler compiler, String command, String filepath, String outputDir) {
        super(compiler, command);
        this.filepath = filepath;
        this.outputDir = outputDir;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "unzip", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(session.getAnalysis().replaceShellVariable(session, context, this.getScript(), true, true));
        }

        File file = PathExpression.toFile(session, context, this.filepath);
        File outputDir = this.outputDir == null ? session.getDirectory() : PathExpression.toFile(session, context, this.outputDir);

        this.compress = new ZipCompress();
        try {
            this.compress.setFile(file);
            this.compress.setLogWriter(new ScriptOutputWriter(stdout, context.getCharsetName()));
            this.compress.extract(outputDir, Settings.getFileEncoding());

            session.setValue(file);
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
