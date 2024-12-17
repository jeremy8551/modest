package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.annotation.ScriptCommand;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.ResourcesUtils;

@ScriptCommand(name = "bye", keywords = {"bye"})
public class ByeCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        if ("bye".equalsIgnoreCase(command)) {
            return new ByeCommand(this, orginalScript);
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr109", command, "bye"));
        }
    }

}
