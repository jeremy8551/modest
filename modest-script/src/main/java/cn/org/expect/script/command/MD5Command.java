package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.crypto.MD5Encrypt;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.io.PathExpression;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminate;

/**
 * 生成字符串或文件的 MD5 值
 */
public class MD5Command extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported, Terminate {

    /** 文件绝对路径 */
    private String filepath;

    public MD5Command(UniversalCommandCompiler compiler, String command, String filepath) {
        super(compiler, command);
        this.filepath = filepath;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "md5sum", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        if (print) {
            File file = PathExpression.toFile(session, context, this.filepath);
            if (file.exists() && file.isFile()) {
                String md5 = MD5Encrypt.encrypt(file, this);
                stdout.println(md5);
                session.setValue(md5);
            } else {
                UniversalScriptAnalysis analysis = session.getAnalysis();
                String md5 = MD5Encrypt.encrypt(analysis.replaceShellVariable(session, context, this.filepath, true, !analysis.containsQuotation(this.filepath)), this);
                stdout.println(md5);
                session.setValue(md5);
            }
        }
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
