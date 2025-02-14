package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.LoginExpression;
import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "ssh", keywords = {"ssh"})
public class SSH2CommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        WordIterator it = analysis.parse(command);
        String login = it.readUntil("&&"); // ssh username@host:port?password=
        String oscommand = it.readOther();

        LoginExpression expr = new LoginExpression(analysis, login);
        String host = expr.getLoginHost();
        String port = expr.getLoginPort();
        String username = expr.getLoginUsername();
        String password = expr.getLoginPassword();
        return new SSH2Command(this, orginalScript, host, port, username, password, oscommand);
    }
}
