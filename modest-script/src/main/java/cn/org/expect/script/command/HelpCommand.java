package cn.org.expect.script.command;

import java.io.File;
import java.io.InputStream;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.IO;

/**
 * 打印脚本引擎的使用说明
 */
public class HelpCommand extends AbstractTraceCommand implements NohupCommandSupported {

    public HelpCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        String charsetName = context.getCharsetName();
        InputStream in = UniversalScriptEngine.class.getResourceAsStream("README.md");
        stdout.println(new String(IO.read(in), charsetName));
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
