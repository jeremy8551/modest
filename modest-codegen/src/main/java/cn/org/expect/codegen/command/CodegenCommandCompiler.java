package cn.org.expect.codegen.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.AbstractTraceCommand;
import cn.org.expect.script.command.AbstractTraceCommandCompiler;

/**
 * 编译 codegen 脚本命令并创建对应的可执行命令
 */
@EasyCommandCompiler(name = "codegen", keywords = {"codegen"})
public class CodegenCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String originalScript, String command) {
        CommandExpression expression = new CommandExpression(analysis, "codegen {1}", command);
        return new CodegenCommand(this, originalScript, expression.getParameter());
    }
}
