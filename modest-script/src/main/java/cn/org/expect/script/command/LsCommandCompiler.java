package cn.org.expect.script.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

/**
 * 编译 ls 脚本命令并创建对应的可执行命令
 */
@EasyCommandCompiler(name = "ls", keywords = {"ls"})
public class LsCommandCompiler extends AbstractFileCommandCompiler {

    /** {@inheritDoc} */
    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    /** {@inheritDoc} */
    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expression = new CommandExpression(analysis, "ls -l ", command);
        List<String> list = new ArrayList<String>(expression.getParameters());
        return new LsCommand(this, orginalScript, list, expression.containsOption("-l"));
    }
}
