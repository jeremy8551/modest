package cn.org.expect.script.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.io.BufferedLineReader;
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

/**
 * 读取字符串前几行内容 <br>
 * <br>
 * head -n 1 /home/user/file.txt
 *
 * @author jeremy8551@gmail.com
 */
public class HeadCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 前几行数据 */
    private int line;

    /** 管道输入参数 */
    private String parameter;

    /** 文件字符集编码 */
    private String charsetName;

    /** 文件绝对路径 */
    private String filepath;

    public HeadCommand(UniversalCommandCompiler compiler, String command, int line, String parameter, String charsetName, String filepath) {
        super(compiler, command);
        this.line = line;
        this.parameter = parameter;
        this.charsetName = charsetName;
        this.filepath = filepath;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.filepath)) {
            this.parameter = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "head", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        int count = 0;
        boolean print = session.isEchoEnable() || forceStdout;
        if (session.getAnalysis().isBlank(this.filepath)) {
            BufferedLineReader in = new BufferedLineReader(this.parameter);
            try {
                while (in.hasNext()) {
                    String line = in.next();
                    if (++count <= this.line && print) {
                        stdout.println(line);
                    }
                }
            } finally {
                in.close();
            }
        } else {
            File file = PathExpression.toFile(session, context, this.filepath);
            BufferedReader in = IO.getBufferedReader(file, StringUtils.coalesce(this.charsetName, context.getCharsetName()));
            try {
                String line;
                while ((line = in.readLine()) != null && ++count <= this.line && print) {
                    stdout.println(line);
                }
            } finally {
                in.close();
            }
        }
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
