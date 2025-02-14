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
 * 打印脚本引擎中最近一次发生的异常信息 <br>
 * 脚本命令规则: stacktrace -s -l <br>
 * -s 选项表示打印发生异常的脚本语句 <br>
 * -l 选项表示打印发生异常的脚本语句所在行号
 *
 * @author jeremy8551@gmail.com
 */
@EasyCommandCompiler(name = "stacktrace", keywords = {"stacktrace"})
public class StacktraceCommandCompiler extends AbstractTraceCommandCompiler {

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "stacktrace -s -l", command);
        return new StacktraceCommand(this, orginalScript, expr.containsOption("-s"), expr.containsOption("-l"));
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }
}
