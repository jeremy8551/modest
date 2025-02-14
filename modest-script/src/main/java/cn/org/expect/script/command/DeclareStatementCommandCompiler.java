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

@EasyCommandCompiler(name = "declare", keywords = {"declare", "statement"})
public class DeclareStatementCommandCompiler extends AbstractCommandCompiler {

    public final static String REGEX = "^(?i)\\s*declare\\s+\\S+\\s+statement\\s+.*with\\s+.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readMultilineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        String batch = "5000";
        WordIterator it = analysis.parse(command);
        it.assertNext("declare");
        String name = it.next();
        it.assertNext("statement");
        if (it.isNext("by")) {
            it.assertNext("by");
            batch = it.next();
            it.assertNext("batch");
        }
        it.assertNext("with");
        String sql = it.readOther();
        return new DeclareStatementCommand(this, command, name, sql, batch);
    }
}
