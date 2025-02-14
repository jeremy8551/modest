package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.internal.CommandList;

@EasyCommandCompiler(name = "declare", keywords = {"declare", "global", "handler", "begin", "end", UniversalScriptVariable.VARNAME_SQLSTATE, UniversalScriptVariable.VARNAME_EXCEPTION, UniversalScriptVariable.VARNAME_ERRORCODE, UniversalScriptVariable.VARNAME_EXITCODE, UniversalScriptVariable.VARNAME_ERRORSCRIPT})
public class DeclareHandlerCommandCompiler extends AbstractGlobalCommandCompiler {

    public final static String REGEX = "^(?i)\\s*declare\\s+([global\\s+]*)(\\S+)\\s+handler\\s+for\\s+(.*)\\s+begin\\s*.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readPieceofScript("begin", "end");
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("declare");
        boolean global = it.isNext("global");
        if (global) {
            it.assertNext("global");
        }

        String exitOrContinue = it.next();
        it.assertNext("handler");
        it.assertNext("for");

        String condition = it.readUntil("begin");
        boolean isExitHandler = analysis.indexOf(condition, "exitcode", 0, 1, 1) != -1;
        if (!isExitHandler // 异常处理逻辑的执行条件必须要有关键字
            && analysis.indexOf(condition, "exception", 0, 1, 1) == -1 //
            && analysis.indexOf(condition, "sqlstate", 0, 1, 1) == -1 //
            && analysis.indexOf(condition, "errorcode", 0, 1, 1) == -1 //
            && analysis.indexOf(condition, "exitcode", 0, 1, 1) == -1 //
        ) {
            throw new UniversalScriptException("script.stderr.message054", command, "exception", "sqlstate", "errorcode", "exitcode");
        }

        it.assertLast("end");
        String body = it.readOther();
        List<UniversalScriptCommand> commands = parser.read(body);
        for (UniversalScriptCommand cmd : commands) { // 在 declare handler 语句中不能使用的语句
            if ((cmd instanceof LoopCommandSupported) && !((LoopCommandSupported) cmd).enableLoop()) {
                throw new UniversalScriptException("script.stderr.message027", command, cmd.getScript());
            }
        }

        CommandList cmdlist = new CommandList("declareHandlerFor", commands, command);
        return new DeclareHandlerCommand(this, command, cmdlist, exitOrContinue, condition, isExitHandler, global);
    }
}
