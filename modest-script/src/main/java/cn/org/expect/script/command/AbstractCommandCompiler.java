package cn.org.expect.script.command;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;

public abstract class AbstractCommandCompiler implements UniversalCommandCompiler {
    protected final static Log log = LogFactory.getLog(AbstractCommandCompiler.class);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return UniversalCommandCompilerResult.NEUTRAL;
    }

    public abstract String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws Exception;

    public abstract UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String script) throws Exception;
}
