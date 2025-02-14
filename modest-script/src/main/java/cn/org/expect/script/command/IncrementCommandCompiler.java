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
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "extract", keywords = {"extract"})
public class IncrementCommandCompiler extends AbstractTraceCommandCompiler {

    public final static String REGEX = "^(?i)extract\\s+increment\\s+compare\\s+.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readMultilineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws Exception {
        WordIterator it = analysis.parse(analysis.replaceShellVariable(session, context, command, true, true));
        it.assertNext("extract");
        it.assertNext("increment");
        it.assertNext("compare");
        String newfileExpr = it.readUntil("and");
        String oldfileExpr = it.readUntil("write");
        String script = it.readOther();
        String[] array = StringUtils.removeBlank(StringUtils.split(script, ArrayUtils.asList("write"), analysis.ignoreCase()));

        IncrementExpression newfileexpr = new IncrementExpression(session, context, newfileExpr);
        IncrementExpression oldfileexpr = new IncrementExpression(session, context, oldfileExpr);
        IncrementExpression[] writeExpr = new IncrementExpression[array.length];
        for (int i = 0; i < array.length; i++) {
            String expression = array[i];
            writeExpr[i] = new IncrementExpression(session, context, expression);
        }
        return new IncrementCommand(this, orginalScript, newfileexpr, oldfileexpr, writeExpr);
    }
}
