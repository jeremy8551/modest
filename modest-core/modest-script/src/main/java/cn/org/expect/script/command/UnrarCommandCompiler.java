package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;

@ScriptCommand(name = "unrar", keywords = {"unrar"})
public class UnrarCommandCompiler extends AbstractFileCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "unrar {0-1}", command);
        String filepath = expr.getParameter();
        return new UnrarCommand(this, orginalScript, filepath);
    }

}