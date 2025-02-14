package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "java", keywords = {"java"})
public class JavaCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        WordIterator it = analysis.parse(command);
        it.assertNext("java");
        String className = it.next(); // JAVA 类名
        if (className == null) {
            throw new UniversalScriptException("script.stderr.message016", command);
        } else {
            List<String> args = it.asList(); // 截取参数
            return new JavaCommand(this, orginalScript, className, args);
        }
    }
}
