package cn.org.expect.script.command;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.org.expect.expression.LoginExpression;
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
public class DeclareSSHClientCommandCompiler extends AbstractCommandCompiler {

    public final static String REGEX = "^(?i)\\s*declare\\s+(\\S+)\\s+ssh\\s+client\\s+for\\s+connect\\s+to\\s+([^\\;|\\s]+)\\s*[\\;]*.*";

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
        it.assertNext("client");
        it.assertNext("for");
        it.assertNext("connect");
        it.assertNext("to");

        String part = it.readOther();
        LoginExpression expr = new LoginExpression(analysis, "ssh " + part);
        String host = expr.getLoginHost();
        String port = expr.getLoginPort();
        String username = expr.getLoginUsername();
        String password = expr.getLoginPassword();
        return new DeclareSSHClientCommand(this, command, name, host, port, username, password);
    }
}
