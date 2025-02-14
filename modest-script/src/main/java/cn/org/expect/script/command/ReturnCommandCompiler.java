package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "return", keywords = {"return"})
public class ReturnCommandCompiler extends AbstractSlaveCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        String returnCode = analysis.trim(command.substring("return".length()), 0, 1);
        if (returnCode.length() == 0) {
            throw new UniversalScriptException("script.stderr.message075", command);
        } else {
            return new ReturnCommand(this, orginalScript, returnCode);
        }
    }
}
