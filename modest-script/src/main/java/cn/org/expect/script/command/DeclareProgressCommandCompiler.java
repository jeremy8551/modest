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

@EasyCommandCompiler(name = "declare", keywords = {"declare", "global", "progress"})
public class DeclareProgressCommandCompiler extends AbstractGlobalCommandCompiler {

    public final static String REGEX = "^(?i)\\s*declare\\s+([\\S+\\s+]*)progress\\s+use\\s+(.*)\\s+print\\s+(.*)\\s+total\\s+(\\S+)\\s+times\\s*[\\;]*.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        WordIterator it = analysis.parse(session.getAnalysis().replaceShellVariable(session, context, command, true, true));
        it.assertNext("declare");
        boolean global = it.isNext("global");
        if (global) {
            it.assertNext("global");
        }

        String name = it.isNext("progress") ? null : it.next(); // 可能没有进度输出ID编号
        it.assertNext("progress");
        it.assertNext("use");
        String type = it.next();
        it.assertNext("print");
        String message = it.readUntil("total");
        String number = it.next();
        it.assertNext("times");
        it.assertOver();

        return new DeclareProgressCommand(this, command, name, type, message, number, global);
    }
}
