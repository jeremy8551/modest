package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.compress.RarCompress;
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

public class UnrarCommand extends AbstractFileCommand implements UniversalScriptInputStream, JumpCommandSupported, NohupCommandSupported {

    /** 文件绝对路径 */
    private String filepath;

    /** 解压文件的目录 */
    private String outputDir;

    /** 打印日志 */
    private boolean verbose;

    private volatile RarCompress compress;

    public UnrarCommand(UniversalCommandCompiler compiler, String command, String rarFilepath, String outputDir, boolean verbose) {
        super(compiler, command);
        this.filepath = rarFilepath;
        this.outputDir = outputDir;
        this.verbose = verbose;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "unrar", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        File rarfile = PathExpression.toFile(session, context, this.filepath);
        File outputDir = this.outputDir == null ? session.getDirectory() : PathExpression.toFile(session, context, this.outputDir);

        this.compress = new RarCompress();
        try {
            this.compress.setLogWriter(new ScriptOutputWriter(stdout, context.getCharsetName()));
            this.compress.setVerbose(this.verbose);
            this.compress.setFile(rarfile);
            this.compress.extract(outputDir, Settings.getFileEncoding());

            session.setValue(rarfile);
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
