package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "date", keywords = {"date", "day", "month", "year", "minute", "hour", "second", "millisecond"})
public class DateCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        int option = analysis.indexOf(command, "-d", 0, 0, 0);
        int index = analysis.indexOf(command, new char[]{'+', '-'}, option == -1 ? 0 : option + 1);
        String formula = analysis.trim((index == -1) ? null : command.substring(index), 0, 1);
        String date = (index == -1) ? command : command.substring(0, index);

        CommandExpression expr = new CommandExpression(analysis, "date -d:", date);
        String pattern = expr.getParameter();
        String dateStr = expr.containsOption("-d") ? expr.getOptionValue("-d") : null;
        return new DateCommand(this, orginalScript, formula, dateStr, pattern);
    }
}
