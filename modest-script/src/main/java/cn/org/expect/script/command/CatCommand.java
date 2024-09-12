package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

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
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

public class CatCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    private String charsetName;

    private String filepath;

    public CatCommand(UniversalCommandCompiler compiler, String command, String charsetName, String filepath) {
        super(compiler, command);
        this.charsetName = charsetName;
        this.filepath = filepath;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlankline(this.filepath)) {
            this.filepath = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr014", this.command, "cat", this.filepath));
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        ScriptFile file = new ScriptFile(session, context, this.filepath);
        String content = FileUtils.readline(file, StringUtils.defaultString(this.charsetName, context.getCharsetName()), 0);

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(content);
        }

        session.removeValue();
        session.putValue("cat", content);
        return 0;
    }

    public void terminate() throws Exception {
    }

    public boolean enableNohup() {
        return true;
    }

}
