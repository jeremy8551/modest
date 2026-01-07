package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "zip", keywords = {"zip"})
public class ZipCommandCompiler extends AbstractFileCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "zip -rvm {1-2}", command);
        String zipFile = expr.getParameters().size() == 1 ? null : expr.getParameter(1);
        String filepath = expr.getParameters().size() == 1 ? expr.getParameter(1) : expr.getParameter(2);
        return new ZipCommand(this, orginalScript, zipFile, filepath, expr.containsOption("-r"), expr.containsOption("-v"), expr.containsOption("-m"));
    }
}
