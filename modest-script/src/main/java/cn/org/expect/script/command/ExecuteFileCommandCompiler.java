package cn.org.expect.script.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.io.PathExpression;

@EasyCommandCompiler(name = {"."}, keywords = {UniversalScriptVariable.SESSION_VARNAME_THIS, UniversalScriptVariable.SESSION_VARNAME_SCRIPTFILE, UniversalScriptVariable.SESSION_VARNAME_HOME, UniversalScriptVariable.SESSION_VARNAME_PWD, UniversalScriptVariable.SESSION_VARNAME_OLDPWD, UniversalScriptVariable.SESSION_VARNAME_TEMP})
public class ExecuteFileCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        String expression = analysis.trim(command.substring(".".length()), 0, 1); // 表达式
        List<String> list = analysis.split(expression, new ArrayList<String>());
        for (int i = 1; i < list.size(); i++) { // 从第二个元素开始
            String str = list.get(i);
            list.set(i, analysis.replaceShellVariable(session, context, analysis.unQuotation(str), true, !analysis.containsQuotation(str)));
        }

        PathExpression expr = new PathExpression(session, context, list.get(0));
        String[] parameters = list.toArray(new String[list.size()]);
        return new ExecuteFileCommand(this, orginalScript, expr, parameters);
    }
}
