package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "wc")
public class WcCommandCompiler extends AbstractFileCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "wc --lang -clw {0-1}", command);
        String charsetName = expr.getOptionValue("-lang");
        String filepath = analysis.trim(expr.getParameter(), 0, 1);
        boolean words = expr.containsOption("-w");
        boolean bytes = expr.containsOption("-c");
        boolean lines = expr.containsOption("-l");

        if (!lines && !words && !bytes) {
            words = true;
            bytes = true;
            lines = true;
        }
        return new WcCommand(this, orginalScript, filepath, charsetName, filepath, words, bytes, lines);
    }
}
