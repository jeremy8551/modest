package cn.org.expect.script.command;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

/**
 * 编译 undeclare 脚本命令并创建对应的可执行命令
 */
@EasyCommandCompiler(name = "undeclare", keywords = {"undeclare"})
public class UndeclareHandlerCommandCompiler extends AbstractGlobalCommandCompiler {

    public final static String REGEX = "^(?i)\\s*undeclare\\s+((?:global\\s+)?)handler\\s+for\\s+.+";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    /** {@inheritDoc} */
    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).matches() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    /** {@inheritDoc} */
    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    /** {@inheritDoc} */
    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("undeclare");
        boolean global = it.isNext("global");
        if (global) {
            it.assertNext("global");
        }
        it.assertNext("handler");
        it.assertNext("for");

        String condition = it.readOther();
        boolean isExitHandler = analysis.indexOf(condition, "exitcode", 0, 1, 1) != -1;
        return new UndeclareHandlerCommand(this, command, condition, isExitHandler, global);
    }
}
