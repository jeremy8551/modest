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
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "while", keywords = {"while", "read", "do", "done"})
public class ReadCommandCompiler extends AbstractCommandCompiler {

    public final static String REGEX = "^(?i)\\s*while\\s+read\\s+\\S+\\s+do\\s*.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        String loopScript = in.readPieceofScript("do", "done");
        String inputScript = in.readSinglelineScript(); // 读取 < source 表达式
        String script = loopScript + " " + inputScript; // 完整的语句
        if (StringUtils.isNotBlank(loopScript) && StringUtils.isNotBlank(inputScript) && analysis.startsWith(inputScript, "<", 0, true)) {
            return script;
        } else {
            throw new UniversalScriptException("script.stderr.message071", script, "<");
        }
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("while");
        it.assertNext("read");
        String name = it.next(); // 变量名
        it.assertNext("do");
        String filepath = it.last(); // 读取 < filepath
        if (filepath.startsWith("<")) {
            filepath = filepath.substring(1); // 删除第一个字符 <
        } else {
            it.assertLast("<");
        }
        it.assertLast("done");
        String body = it.readOther();
        List<UniversalScriptCommand> commands = parser.read(body); // 读取循环体中的代码

        // 在语句中不能使用的语句
        for (UniversalScriptCommand cmd : commands) {
            if ((cmd instanceof LoopCommandSupported) && !((LoopCommandSupported) cmd).enableLoop()) {
                throw new UniversalScriptException("script.stderr.message027", command, cmd.getScript());
            }
        }

        CommandList cmdlist = new CommandList(name, commands, command);
        return new ReadCommand(this, command, cmdlist, filepath);
    }
}
