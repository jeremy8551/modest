package cn.org.expect.script.command;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "db")
public class DBConnectCommandCompiler extends AbstractTraceCommandCompiler {

    public final static String REGEX = "^(?i)\\s*db\\s+connect\\s+([^\\;]+)\\s*[\\;]*.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        WordIterator it = analysis.parse(command);
        it.assertNext("db");
        it.assertNext("connect");

        if (it.isNext("to")) {
            it.assertNext("to");
            String name = it.readOther();
            return new DBConnectCommand(this, orginalScript, name);
        }

        if (it.isNext("reset")) {
            String next = it.readOther();
            return new DBConnectCommand(this, orginalScript, analysis.replaceShellVariable(session, context, next, true, true));
        } else {
            throw new UniversalScriptException("script.stderr.message132", orginalScript, "db connect", StringUtils.join(ArrayUtils.as("to", "reset"), " || "));
        }
    }
}
