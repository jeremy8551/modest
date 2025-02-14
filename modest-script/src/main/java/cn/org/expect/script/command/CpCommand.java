package cn.org.expect.script.command;

import java.io.File;
import java.io.FileOutputStream;
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
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.script.io.ScriptFileExpression;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

public class CpCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    private String srcFileExpression;

    private String destFileExpression;

    public CpCommand(UniversalCommandCompiler compiler, String command, String srcFileExpression, String destFileExpression) {
        super(compiler, command);
        this.srcFileExpression = srcFileExpression;
        this.destFileExpression = destFileExpression;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.srcFileExpression)) {
            this.srcFileExpression = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "cat", this.destFileExpression);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        ScriptFileExpression srcfile = new ScriptFileExpression(session, context, this.srcFileExpression);
        File dest = new ScriptFile(session, context, this.destFileExpression); // 目标文件/目录

        if (session.isEchoEnable() || forceStdout) {
            stdout.println("cp " + srcfile.getAbsolutePath() + " " + dest.getAbsolutePath());
        }

        if (srcfile.isResource()) {
            File file;
            if (dest.exists()) {
                if (dest.isDirectory()) {
                    file = new File(dest, srcfile.getName());
                } else {
                    file = dest;
                }
            } else {
                file = dest;
            }

            FileUtils.assertCreateFile(file);
            session.putValue(file);
            IO.write(srcfile.getInputStream(), new FileOutputStream(file, false), null);
            return 0;
        } else {
            File src = new File(srcfile.getAbsolutePath());
            FileUtils.assertExists(src);

            File file;
            if (src.isDirectory()) {
                if (dest.exists()) {
                    FileUtils.assertDirectory(dest);
                    file = new File(dest, srcfile.getName());
                } else {
                    file = FileUtils.assertCreateDirectory(dest);
                }
            } else {
                if (dest.exists()) {
                    if (dest.isDirectory()) {
                        file = new File(dest, srcfile.getName());
                    } else {
                        file = dest;
                    }
                } else {
                    file = dest;
                }

                FileUtils.assertCreateFile(file);
            }

            session.putValue(file);
            return FileUtils.copy(src, file) ? 0 : UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
