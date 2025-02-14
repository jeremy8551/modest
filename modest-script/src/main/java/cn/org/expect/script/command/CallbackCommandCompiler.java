package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.internal.CommandList;

/**
 * 用户自定义回调函数 <br>
 * <br>
 * 语法: declare global command callback for exit | quit | echo | step begin .. end
 *
 * @author jeremy8551@gmail.com
 */
@EasyCommandCompiler(name = "declare", keywords = {"declare", "global"})
public class CallbackCommandCompiler extends AbstractGlobalCommandCompiler {

    /** 正则表达式 */
    public final static String REGEX = "^(?i)\\s*declare\\s+([global\\s+]*)command\\s+callback\\s+for\\s+(.*)\\s+begin\\s*.*";

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
        it.assertNext("command");
        it.assertNext("callback");
        it.assertNext("for");
        String commandExpr = it.readUntil("begin");
        UniversalCommandCompiler compiler = session.getCompiler().getRepository().get(analysis, commandExpr);
        if (compiler == null) {
            throw new UniversalScriptException("script.stderr.message042", commandExpr);
        }
        Class<?> type = compiler.getClass();

        it.assertLast("end");
        String body = it.readOther();
        List<UniversalScriptCommand> commands = parser.read(body);
        for (UniversalScriptCommand cmd : commands) { // 在 declare handler 语句中不能使用的语句
            if ((cmd instanceof LoopCommandSupported) && !((LoopCommandSupported) cmd).enableLoop()) {
                throw new UniversalScriptException("script.stderr.message027", command, cmd.getScript());
            }
        }

        CommandList cmdlist = new CommandList(CallbackCommand.NAME, commands, command);
        return new CallbackCommand(this, command, type, cmdlist, global);
    }
}
