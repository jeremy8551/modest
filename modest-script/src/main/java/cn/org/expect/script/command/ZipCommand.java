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
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

public class ZipCommand extends AbstractFileCommand implements UniversalScriptInputStream, JumpCommandSupported, NohupCommandSupported {

    /** zip文件路径 */
    private String zipFilepath;

    /** 文件绝对路径 */
    private String filepath;

    /** zip压缩 */
    private volatile ZipCompress compress;

    /** true表示使用递归压缩 */
    private boolean rOption;

    /** true表示打印日志 */
    private boolean vOption;

    /** true表示使用移动模式 */
    private boolean mOption;

    public ZipCommand(UniversalCommandCompiler compiler, String command, String zipFilepath, String filepath, boolean rOption, boolean vOption, boolean mOption) {
        super(compiler, command);
        this.zipFilepath = zipFilepath;
        this.filepath = filepath;
        this.rOption = rOption;
        this.vOption = vOption;
        this.mOption = mOption;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "zip", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(session.getAnalysis().replaceShellVariable(session, context, this.getScript(), true, true));
        }

        File file = PathExpression.toFile(session, context, this.filepath);
        File zipfile = this.zipFilepath == null ? new File(file.getParentFile(), FileUtils.changeFilenameExt(file.getName(), "zip")) : PathExpression.toFile(session, context, this.zipFilepath);
        this.compress = new ZipCompress();
        try {
            this.compress.setFile(zipfile);
            this.compress.setLogWriter(new ScriptOutputWriter(stdout, context.getCharsetName()));
            this.compress.setVerbose(this.vOption);
            this.compress.setRecursion(this.rOption);
            this.compress.setMobileMode(this.mOption);
            this.compress.archiveFile(file, null);

            session.setValue(zipfile);
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
