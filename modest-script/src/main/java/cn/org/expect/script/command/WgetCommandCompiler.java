package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "wget")
public class WgetCommandCompiler extends AbstractFileCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "wget [-P:|-n] [-O:|-n] {0-1}", command);
        int size = expr.getParameters().size();
        String target = expr.getOptionValue("-P");
        String filename = expr.getOptionValue("-O");
        boolean echoName = expr.containsOption("-n");

        if (size == 1) {
            return new WgetCommand(this, orginalScript, expr.getParameter(1), target, filename, echoName);
        } else {
            return new WgetCommand(this, orginalScript, null, target, filename, echoName);
        }
    }
}
