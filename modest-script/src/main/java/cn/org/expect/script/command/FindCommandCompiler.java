package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "find", keywords = {"find"})
public class FindCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "find -hdpR -enos: {1}", command);
        String filepath = expr.getParameter();
        String name = expr.getOptionValue("-n");
        Ensure.isTrue(expr.containsOption("-n"));

        boolean loop = !expr.containsOption("-R");
        boolean hidden = expr.containsOption("-h");
        boolean distinct = expr.containsOption("-d");
        boolean position = expr.containsOption("-p");
        String encoding = expr.containsOption("-e") ? expr.getOptionValue("-e") : CharsetUtils.get();
        String outputFile = expr.containsOption("-o") ? expr.getOptionValue("-o") : "";
        String outputDelimiter = expr.containsOption("-s") ? StringUtils.coalesce(expr.getOptionValue("-s"), "\n") : "\n";
        return new FindCommand(this, orginalScript, filepath, name, encoding, outputFile, outputDelimiter, loop, hidden, distinct, position);
    }
}
