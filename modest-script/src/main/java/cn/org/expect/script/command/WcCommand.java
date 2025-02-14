package cn.org.expect.script.command;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.io.BufferedLineReader;
import cn.org.expect.io.TextTableFileCounter;
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
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 显示文件的行数 字数 字节数 文件名 <br>
 * <br>
 * wc -l /home/user/file.txt <br>
 * wc -w 统计字符数 <br>
 * wc -c 统计字节数 <br>
 * wc -l 统计行数 <br>
 *
 * @author jeremy8551@gmail.com
 */
public class WcCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 文件字符集编码 */
    private final String charsetName;

    /** 文件绝对路径 */
    private final String filepath;

    /** 管道输入参数 */
    private String pipeInput;

    /** true表示统计字数 */
    private final boolean words;

    /** true表示统计字节数 */
    private final boolean bytes;

    /** true表示统计行数 */
    private final boolean lines;

    /** true表示使用管道输入字符 false表示不使用管道输入 */
    private boolean pipe;

    public WcCommand(UniversalCommandCompiler compiler, String command, String filepath, String charsetName, String pipeInput, boolean words, boolean bytes, boolean lines) {
        super(compiler, command);
        this.filepath = filepath;
        this.charsetName = charsetName;
        this.pipeInput = pipeInput;
        this.words = words;
        this.bytes = bytes;
        this.lines = lines;
        this.pipe = false;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        this.pipe = analysis.isBlank(this.filepath);
        if (this.pipe) {
            this.pipeInput = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "wc", this.filepath);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        long rows = 0, words = 0, bytes = 0;
        String filepath = "";
        String charsetName = StringUtils.coalesce(this.charsetName, context.getCharsetName());

        if (this.pipe) {
            // 统计行数
            if (this.lines) {
                BufferedReader in = IO.getBufferedReader(new CharArrayReader(this.pipeInput.toCharArray()));
                try {
                    while (in.readLine() != null) {
                        if (this.terminate) {
                            break;
                        } else {
                            rows++;
                        }
                    }
                } finally {
                    in.close();
                }
            }

            // 统计字符个数
            if (this.words) {
                words = this.pipeInput.length();
            }

            // 统计字节个数
            if (this.bytes) {
                bytes = StringUtils.length(this.pipeInput, charsetName);
            }
        } else {
            File file = new ScriptFile(session, context, this.filepath);

            // 统计字节个数
            if (this.bytes) {
                bytes = file.length();
            }

            // 统计行数
            if (this.lines && !this.words) {
                ThreadSource source = context.getContainer().getBean(ThreadSource.class);
                TextTableFileCounter counter = new TextTableFileCounter(source, 2);
                rows = counter.execute(file, charsetName);
            } else {
                BufferedLineReader in = new BufferedLineReader(file, charsetName);
                try {
                    words = in.skip(file.length());
                    rows = in.getLineNumber();
                } finally {
                    in.close();
                }
            }

            filepath = file.getAbsolutePath();
        }

        StringBuilder buf = new StringBuilder(50);
        if (this.lines) {
            buf.append(StringUtils.right(rows, 10, ' '));
            session.putValue(rows);
        }

        if (this.words) {
            buf.append(StringUtils.right(words, 10, ' '));
            session.putValue(words);
        }

        if (this.bytes) {
            buf.append(StringUtils.right(bytes, 10, ' '));
            session.putValue(bytes);
        }

        if (filepath.length() > 0) {
            buf.append(' ').append(filepath);
        }

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(buf);
        }
        return this.terminate ? UniversalScriptCommand.TERMINATE : 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
