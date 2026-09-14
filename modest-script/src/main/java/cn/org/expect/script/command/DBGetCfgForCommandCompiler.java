package cn.org.expect.script.command;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

/**
 * 编译 db 脚本命令并创建对应的可执行命令
 */
@EasyCommandCompiler(name = "db")
public class DBGetCfgForCommandCompiler extends AbstractTraceCommandCompiler {

    public final static String REGEX = "^(?i)\\s*db\\s+get\\s+cfg\\s+for\\s+(.+)";

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
    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        WordIterator it = analysis.parse(analysis.replaceShellVariable(session, context, command, true, true));
        it.assertNext("db");
        it.assertNext("get");
        it.assertNext("cfg");
        it.assertNext("for");
        String information = it.readOther();
        return new DBGetCfgForCommand(this, command, information);
    }
}
