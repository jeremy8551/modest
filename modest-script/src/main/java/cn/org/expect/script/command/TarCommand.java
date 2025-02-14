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
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import cn.org.expect.zip.Compress;

public class TarCommand extends AbstractFileCommand implements UniversalScriptInputStream, JumpCommandSupported, NohupCommandSupported {

    /** 文件绝对路径 */
    private String filepath;

    /** true 表示压缩文件, false 表示解压文件 */
    private boolean isCompress;

    /** 压缩接口 */
    private Compress compress;

    public TarCommand(UniversalCommandCompiler compiler, String command, String filepath, boolean isCompress) {
        super(compiler, command);
        this.filepath = filepath;
        this.isCompress = isCompress;
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
        } else {
            return this.decompressFile(session, context, stdout, stderr, forceStdout);
        }
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
        File file = new ScriptFile(session, context, this.filepath);
        File tarfile = new File(file.getParentFile(), FileUtils.changeFilenameExt(file.getName(), "tar"));

        if (session.isEchoEnable() || forceStdout) {
            stdout.println("tar -zcvf " + file.getAbsolutePath());
        }

        this.compress = context.getContainer().getBean(Compress.class, "tar");
        try {
            this.compress.setFile(tarfile);
            this.compress.archiveFile(file, null);

            session.putValue(tarfile);

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
        File tarfile = new ScriptFile(session, context, this.filepath);

        if (session.isEchoEnable() || forceStdout) {
            stdout.println("tar -xvf " + tarfile.getAbsolutePath());
        }

        this.compress = context.getContainer().getBean(Compress.class, "tar");
        try {
            this.compress.setFile(tarfile);
            this.compress.extract(tarfile.getParentFile(), Settings.getFileEncoding());

            session.putValue(tarfile);

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
