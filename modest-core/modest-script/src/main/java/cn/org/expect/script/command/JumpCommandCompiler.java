package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.ResourcesUtils;

@ScriptCommand(name = "jump", keywords = {"jump"})
public class JumpCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        String str = command.substring("jump".length());
        String message = analysis.trim(str, 0, 1);

        if (analysis.isBlankline(message)) {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr095", command));
        } else {
            return new JumpCommand(this, orginalScript, message);
        }
    }

}
