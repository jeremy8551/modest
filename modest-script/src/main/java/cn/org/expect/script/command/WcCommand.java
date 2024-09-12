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
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 显示文件的行数 字数 字节数 文件名 <br>
 * <br>
 * wc -l /home/user/file.txt
 *
 * @author jeremy8551@qq.com
 */
public class WcCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 文件字符集编码 */
    private String charsetName;

    /** 文件绝对路径 */
    private String filepath;

    /** 管道输入参数 */
    private String pipeInput;

    /** true表示统计字数 */
    private boolean words;

    /** true表示统计字节数 */
    private boolean bytes;

    /** true表示统计行数 */
    private boolean lines;

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
        this.pipe = analysis.isBlankline(this.filepath);
        if (this.pipe) {
            this.pipeInput = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr014", this.command, "wc", this.filepath));
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        long rows = 0, words = 0, bytes = 0;
        String filepath = "";
        String charsetName = StringUtils.defaultString(this.charsetName, context.getCharsetName());

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

        session.removeValue();
        StringBuilder buf = new StringBuilder(50);
        if (this.lines) {
            buf.append(StringUtils.right(rows, 10, ' '));
            session.putValue("line", rows);
        }

        if (this.words) {
            buf.append(StringUtils.right(words, 10, ' '));
            session.putValue("word", words);
        }

        if (this.bytes) {
            buf.append(StringUtils.right(bytes, 10, ' '));
            session.putValue("byte", bytes);
        }

        if (filepath.length() > 0) {
            buf.append(' ').append(filepath);
//            session.setValue("file", filepath); 不需要返回文件路径
        }

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(buf);
        }
        return this.terminate ? UniversalScriptCommand.TERMINATE : 0;
    }

    public void terminate() {
        this.terminate = true;
    }

    public boolean enableNohup() {
        return true;
    }

}
