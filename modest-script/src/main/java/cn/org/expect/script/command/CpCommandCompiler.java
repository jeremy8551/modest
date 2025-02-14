package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "cp")
public class CpCommandCompiler extends AbstractFileCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "cp {1-2}", command);
        int size = expr.getParameters().size();
        if (size == 1) {
            String dstfile = analysis.trim(expr.getParameter(1), 0, 1);
            return new CpCommand(this, orginalScript, null, dstfile);
        } else {
            String srcfile = analysis.trim(expr.getParameter(1), 0, 1);
            String dstfile = analysis.trim(expr.getParameter(2), 0, 1);
            return new CpCommand(this, orginalScript, srcfile, dstfile);
        }
    }
}
