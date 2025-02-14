package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "tail", keywords = {"tail"})
public class TailCommandCompiler extends AbstractFileCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "tail -n: --lang {0-1}", command);
        String charsetName = expr.getOptionValue("-lang");
        int line = StringUtils.parseInt(expr.getOptionValue("-n"), 10);
        String filepath = analysis.trim(expr.getParameter(), 0, 1);

        if (line <= 0) {
            throw new UniversalScriptException("script.stderr.message068", command, line);
        } else {
            return new TailCommand(this, orginalScript, line, charsetName, filepath);
        }
    }
}
