package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptUsage;

public abstract class AbstractCommandCompiler implements UniversalCommandCompiler {

    public UniversalCommandCompilerResult match(String name, String script) {
        return UniversalCommandCompilerResult.NEUTRAL;
    }

    public abstract String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws Exception;

    public abstract UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String script) throws Exception;

    public void usage(UniversalScriptContext context, UniversalScriptStdout out) {
        out.println(new ScriptUsage(this.getClass()).toString());
    }
}
