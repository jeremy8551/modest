package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.io.ScriptFileExpression;

@EasyCommandCompiler(name = {"."}, keywords = {UniversalScriptVariable.SESSION_VARNAME_SCRIPTNAME, UniversalScriptVariable.SESSION_VARNAME_SCRIPTFILE, UniversalScriptVariable.SESSION_VARNAME_LINESEPARATOR})
public class ExecuteFileCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        String expression = analysis.trim(command.substring(".".length()), 0, 1); // 表达式
        return new ExecuteFileCommand(this, orginalScript, ScriptFileExpression.parse(session, context, expression));
    }
}
