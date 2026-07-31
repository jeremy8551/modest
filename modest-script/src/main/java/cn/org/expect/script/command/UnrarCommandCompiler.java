package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

/**
 * 编译 unrar 脚本命令并创建对应的可执行命令
 */
@EasyCommandCompiler(name = "unrar", keywords = {"unrar"})
public class UnrarCommandCompiler extends AbstractFileCommandCompiler {

    /** {@inheritDoc} */
    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    /** {@inheritDoc} */
    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "unrar -v {1-2}", command);
        String zipFile = expr.getParameter(1);
        String filepath = expr.getParameters().size() == 1 ? null : expr.getParameter(2);
        return new UnrarCommand(this, orginalScript, zipFile, filepath, expr.containsOption("-v"));
    }
}
