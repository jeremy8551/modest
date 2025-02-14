package cn.org.expect.script.command;

import java.io.IOException;
import java.util.ArrayList;
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

@EasyCommandCompiler(name = "fetch", keywords = {"fetch", "into"})
public class FetchCursorCommandCompiler extends AbstractCommandCompiler {

    public final static String REGEX = "^(?i)\\s*fetch\\s+.+\\s+into\\s+.+";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        WordIterator it = analysis.parse(session.getAnalysis().replaceShellVariable(session, context, command, true, true));
        it.assertNext("fetch");
        String name = it.next();
        it.assertNext("into");
        String script = it.readOther();
        List<String> variableNames = new ArrayList<String>();
        analysis.split(script, variableNames, analysis.getSegment());
        for (String variableName : variableNames) {
            if (!context.getEngine().getChecker().isVariableName(variableName)) {
                throw new UniversalScriptException("script.stderr.message069", command, variableName);
            }
        }
        return new FetchCursorCommand(this, command, name, variableNames);
    }
}
