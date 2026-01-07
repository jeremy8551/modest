package cn.org.expect.script.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.io.PathExpression;

@EasyCommandCompiler(name = "daemon")
public class DaemonCommandCompiler extends ExecuteFileCommandCompiler {

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        String expression = analysis.trim(command.substring("daemon".length()), 0, 1); // 表达式
        List<String> list = analysis.split(expression, new ArrayList<String>());
        for (int i = 1; i < list.size(); i++) { // 从第二个元素开始
            String str = list.get(i);
            list.set(i, analysis.replaceShellVariable(session, context, analysis.unQuotation(str), true, !analysis.containsQuotation(str)));
        }

        PathExpression expr = new PathExpression(session, context, list.get(0));
        String[] parameters = list.toArray(new String[list.size()]);
        return new DaemonCommand(this, command, expr, parameters);
    }
}
