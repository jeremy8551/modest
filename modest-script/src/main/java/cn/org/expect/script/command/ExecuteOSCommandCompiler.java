package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.DefaultCommandSupported;

@EasyCommandCompiler(name = "os")
public class ExecuteOSCommandCompiler extends AbstractCommandCompiler implements DefaultCommandSupported {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String script) throws Exception {
        String oscomand = script.substring("os".length());
        return new ExecuteOSCommand(this, script, oscomand);
    }
}
