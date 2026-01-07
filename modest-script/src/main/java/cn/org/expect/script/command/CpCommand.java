package cn.org.expect.script.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

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
import cn.org.expect.script.io.PathExpression;
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
        PathExpression src = new PathExpression(session, context, this.srcFileExpression);
        File destFile = PathExpression.toFile(session, context, this.destFileExpression); // 目标文件/目录

        if (session.isEchoEnable() || forceStdout) {
            stdout.println("cp " + src.getAbsolutePath() + " " + destFile.getAbsolutePath());
        }

        if (src.startWithClasspath()) {
            File file = new File(destFile, src.getName());
            FileUtils.assertCreateFile(file);
            session.setValue(file);
            IO.write(src.getInputStream(), new FileOutputStream(file, false), this);
            return 0;
        } else {
            boolean success = true;
            List<File> files = src.listFiles();
            if (files.isEmpty()) {
                success = false;
                session.setValue(null);
            } else {
                FileUtils.assertDirectory(destFile);
                for (File file : files) {
                    if (!FileUtils.copy(file, new File(destFile, file.getName()))) {
                        success = false;
                    }
                }
                session.setValue(destFile);
            }

            return success ? 0 : UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
