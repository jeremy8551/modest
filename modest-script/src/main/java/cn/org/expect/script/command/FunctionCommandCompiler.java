package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptChecker;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.internal.CommandList;

@EasyCommandCompiler(name = "function", keywords = {"function"})
public class FunctionCommandCompiler extends AbstractCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readPieceofScript("{", "}");
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("function");

        String name = it.next(); // functionName()
        if (!name.endsWith("()")) {
            throw new UniversalScriptException("script.stderr.message065", command);
        }

        String functionName = name.substring(0, name.length() - 2); // 自定义方法名
        UniversalScriptChecker checker = context.getEngine().getChecker();
        if (!checker.isVariableName(functionName)) {
            throw new UniversalScriptException("script.stderr.message066", command, functionName);
        }

        String part = it.readOther(); // { ... }
        String body = analysis.trim(part, 2, 2, '{', '}'); // 删除二侧的大括号
        List<UniversalScriptCommand> commands = parser.read(body);
        for (UniversalScriptCommand cmd : commands) { // 在语句中不能使用的语句
            if ((cmd instanceof LoopCommandSupported) && !((LoopCommandSupported) cmd).enableLoop()) {
                throw new UniversalScriptException("script.stderr.message027", command, cmd.getScript());
            }
        }

        CommandList cmdlist = new CommandList(functionName, commands, command);
        return new FunctionCommand(this, command, cmdlist);
    }
}
