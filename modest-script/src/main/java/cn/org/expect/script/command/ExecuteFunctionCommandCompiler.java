package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptContextAware;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.internal.FunctionSet;

@EasyCommandCompiler(name = "*")
public class ExecuteFunctionCommandCompiler extends AbstractTraceCommandCompiler implements UniversalScriptContextAware {

    private UniversalScriptContext context;

    public void setContext(UniversalScriptContext context) {
        this.context = context;
    }

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String line) {
        FunctionSet local = FunctionSet.get(this.context, false); // 优先从局部域中查
        if (local.contains(name)) {
            return UniversalCommandCompilerResult.NEUTRAL;
        }

        FunctionSet global = FunctionSet.get(this.context, true); // 再次从全局域中查
        if (global.contains(name)) {
            return UniversalCommandCompilerResult.NEUTRAL;
        }

        return UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String script) throws IOException {
        return new ExecuteFunctionCommand(this, orginalScript, script);
    }
}
