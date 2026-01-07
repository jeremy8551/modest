package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.compress.TarCompress;
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

public class TarCommand extends AbstractFileCommand implements UniversalScriptInputStream, JumpCommandSupported, NohupCommandSupported {

    /** 压缩接口 */
    private volatile TarCompress compress;

    /** 文件绝对路径 */
    private String filepath;

    /** true 表示压缩文件, false 表示解压文件 */
    private boolean isCompress;

    /** true 表示解压, false 表示压缩文件 */
    private boolean isExtract;

    /** 使用 zip 压缩 */
    private boolean isZip;

    /** 只看目录结构 */
    private boolean isList;

    /** 输出目录 */
    private String outputDir;

    /** true表示输出日志 */
    private boolean isVerbose;

    public TarCommand(UniversalCommandCompiler compiler, String command, String fOption, boolean cOption, boolean xOption, boolean zOption, boolean tOption, String upperCOption, boolean vOption) {
        super(compiler, command);
        this.filepath = fOption;
        this.isCompress = cOption;
        this.isExtract = xOption;
        this.isZip = zOption;
        this.isList = tOption;
        this.outputDir = upperCOption;
        this.isVerbose = vOption;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "tar", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (this.isCompress) {
            return this.compressFile(session, context, stdout, stderr, forceStdout);
        }

        if (this.isExtract) {
            return this.decompressFile(session, context, stdout, stderr, forceStdout);
        }

        if (this.isList) {
            return this.listInnerFile(session, context, stdout, stderr, forceStdout);
        }

        return 0;
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.compress != null) {
            this.compress.terminate();
        }
    }

    /**
     * 压缩文件
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @return 返回0表示正确, 返回非0表示不正确
     * @throws IOException 执行命令发生错误
     */
    public int compressFile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws IOException {
        File file = PathExpression.toFile(session, context, this.filepath);

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(session.getAnalysis().replaceShellVariable(session, context, this.getScript(), true, true));
        }

        this.compress = new TarCompress();
        try {
            this.compress.setGzipCompress(this.isZip);
            this.compress.setVerbose(this.isVerbose);
            this.compress.setLogWriter(new ScriptOutputWriter(stdout, context.getCharsetName()));

            File tarfile = new File(file.getParentFile(), FileUtils.changeFilenameExt(file.getName(), this.compress.isGzipCompress() ? "tar.gz" : "tar"));
            this.compress.setFile(tarfile);
            this.compress.archiveFile(file, null);

            session.setValue(tarfile);
            return this.compress.isTerminate() ? UniversalScriptCommand.TERMINATE : 0;
        } finally {
            this.compress.close();
        }
    }

    /**
     * 解压文件
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @return 返回0表示正确, 返回非0表示不正确
     * @throws IOException 执行命令发生错误
     */
    public int decompressFile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws IOException {
        File tarfile = PathExpression.toFile(session, context, this.filepath);
        File outputDir = this.outputDir == null ? session.getDirectory() : PathExpression.toFile(session, context, this.outputDir);

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(session.getAnalysis().replaceShellVariable(session, context, this.getScript(), true, true));
        }

        this.compress = new TarCompress();
        try {
            this.compress.setGzipCompress(this.isZip || tarfile.getName().toLowerCase().endsWith(".tar.gz"));
            this.compress.setVerbose(this.isVerbose);
            this.compress.setLogWriter(new ScriptOutputWriter(stdout, context.getCharsetName()));
            this.compress.setFile(tarfile);
            this.compress.setNotExtract(this.isList);
            this.compress.extract(outputDir, context.getCharsetName());

            session.setValue(tarfile);
            return 0;
        } finally {
            this.compress.close();
        }
    }

    /**
     * 列出压缩包中包含的文件（不解压）
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @return 返回0表示正确, 返回非0表示不正确
     * @throws IOException 执行命令发生错误
     */
    public int listInnerFile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws IOException {
        File tarfile = PathExpression.toFile(session, context, this.filepath);
        File outputDir = this.outputDir == null ? session.getDirectory() : PathExpression.toFile(session, context, this.outputDir);

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(session.getAnalysis().replaceShellVariable(session, context, this.getScript(), true, true));
        }

        this.compress = new TarCompress();
        try {
            this.compress.setGzipCompress(this.isZip || tarfile.getName().toLowerCase().endsWith(".tar.gz"));
            this.compress.setVerbose(this.isVerbose);
            this.compress.setLogWriter(new ScriptOutputWriter(stdout, context.getCharsetName()));
            this.compress.setFile(tarfile);
            this.compress.setNotExtract(this.isList);
            this.compress.extract(outputDir, context.getCharsetName());

            session.setValue(tarfile);
            return 0;
        } finally {
            this.compress.close();
        }
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enableJump() {
        return true;
    }
}
