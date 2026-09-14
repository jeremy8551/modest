package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.LoginExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

/**
 * 编译 ftp 脚本命令并创建对应的可执行命令
 */
@EasyCommandCompiler(name = "ftp", keywords = {"ftp"})
public class FtpCommandCompiler extends AbstractFileCommandCompiler {

    /** {@inheritDoc} */
    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    /** {@inheritDoc} */
    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        String str = analysis.replaceShellVariable(session, context, command, true, true);
        LoginExpression expr = new LoginExpression(analysis, str);
        String host = expr.getLoginHost();
        String port = expr.getLoginPort();
        String username = expr.getLoginUsername();
        String password = expr.getLoginPassword();
        return new FtpCommand(this, orginalScript, host, port, username, password);
    }
}
