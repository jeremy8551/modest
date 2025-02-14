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

@EasyCommandCompiler(name = "declare", keywords = {"declare", "ssh"})
public class DeclareSSHTunnelCommandCompiler extends AbstractCommandCompiler {

    public final static String REGEX = "^(?i)\\s*declare\\s+(\\S+)\\s+ssh\\s+tunnel\\s+use\\s+proxy\\s+(.*)\\s+connect\\s+to\\s+([^\\;|\\s]+)\\s*[\\;]*.*";

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
        String name = it.next();
        it.assertNext("ssh");
        it.assertNext("tunnel");
        it.assertNext("use");
        it.assertNext("proxy");
        String proxy = it.readUntil("connect");
        it.assertNext("to");
        String remote = it.readOther();
        return new DeclareSSHTunnelCommand(this, command, name, proxy, remote);
    }
}
