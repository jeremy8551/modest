package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.io.ScriptFileExpression;

@EasyCommandCompiler(name = "daemon")
public class DaemonCommandCompiler extends ExecuteFileCommandCompiler {

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        String expression = analysis.trim(command.substring("daemon".length()), 0, 1); // 表达式
        return new DaemonCommand(this, command, ScriptFileExpression.parse(session, context, expression));
    }
}
