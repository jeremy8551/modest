package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.DefaultCommandSupported;

@EasyCommandCompiler(name = "os", keywords = {"os"})
public class ExecuteOSCommandCompiler extends AbstractTraceCommandCompiler implements DefaultCommandSupported {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        if (analysis.startsWith(command, "os", 0, true)) {
            command = command.substring("os".length());
        }

        return new ExecuteOSCommand(this, orginalScript, command);
    }
}
