package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "terminate", keywords = {"terminate"})
public class TerminateCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "terminate -ps: {0}", command);
        String[] processid = StringUtils.removeBlank(StringUtils.split(analysis.unQuotation(expr.getOptionValue("-p")), analysis.getSegment()));
        String[] sessionid = StringUtils.removeBlank(StringUtils.split(analysis.unQuotation(expr.getOptionValue("-s")), analysis.getSegment()));
        return new TerminateCommand(this, orginalScript, sessionid, processid);
    }
}
