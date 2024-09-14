package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import cn.org.expect.annotation.ScriptCommand;
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
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.CallbackCommandSupported;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.internal.ScriptUsage;
import cn.org.expect.util.ResourcesUtils;

/**
 * 用户自定义回调函数 <br>
 * <br>
 * 语法: declare global command callback for exit | quit | echo | step begin .. end
 *
 * @author jeremy8551@qq.com
 */
@ScriptCommand(name = "declare", keywords = {"declare", "global"})
public class CallbackCommandCompiler extends AbstractGlobalCommandCompiler {

    /** 正则表达式 */
    public final static String REGEX = "^(?i)\\s*declare\\s+([global\\s+]*)command\\s+callback\\s+for\\s+(.*)\\s+begin\\s*.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(String name, String script) {
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
        UniversalCommandCompiler compiler = session.getCompiler().getRepository().get(commandExpr);
        if (compiler == null) {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr047", commandExpr));
        }
        Class<?> cls = compiler.getClass();

        it.assertLast("end");
        String body = it.readOther();
        List<UniversalScriptCommand> commands = parser.read(body);
        for (UniversalScriptCommand cmd : commands) { // 在 declare handler 语句中不能使用的语句
            if ((cmd instanceof LoopCommandSupported) && !((LoopCommandSupported) cmd).enableLoop()) {
                throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr030", "declare command callback", cmd.getScript()));
            }
        }

        CommandList cmdlist = new CommandList(CallbackCommand.NAME, commands);
        return new CallbackCommand(this, command, cls, cmdlist, global);
    }

    public void usage(UniversalScriptContext context, UniversalScriptStdout out) {
        out.println(new ScriptUsage(this.getClass(), CallbackCommandSupported.class.getName()));
    }

}
