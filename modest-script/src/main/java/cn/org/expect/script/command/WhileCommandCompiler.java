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
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.internal.CommandList;

@EasyCommandCompiler(name = "while", keywords = {"while", "loop", "end"})
public class WhileCommandCompiler extends AbstractCommandCompiler {

    public final static String REGEX = "^(?i)\\s*while\\s+[^read]+.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readPieceofScript("loop", "end loop");
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("while");
        String condition = it.readUntil("loop");
        if (analysis.isBlank(condition)) {
            throw new UniversalScriptException("script.stderr.message078", command);
        }
        it.assertLast("loop");
        it.assertLast("end");
        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message030", "while", condition);
        }

        String body = it.readOther();
        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message031", "while", body);
        }

        List<UniversalScriptCommand> commands = parser.read(body);
        for (UniversalScriptCommand cmd : commands) {
            if ((cmd instanceof LoopCommandSupported) && !((LoopCommandSupported) cmd).enableLoop()) { // 在语句中不能使用的语句
                throw new UniversalScriptException("script.stderr.message027", command, cmd.getScript());
            }
        }

        CommandList cmdlist = new CommandList(condition, commands, command);
        return new WhileCommand(this, command, cmdlist);
    }
}
