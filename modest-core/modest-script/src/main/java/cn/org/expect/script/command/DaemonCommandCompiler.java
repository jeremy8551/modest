package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.annotation.ScriptCommand;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.ResourcesUtils;

@ScriptCommand(name = "daemon", keywords = {})
public class DaemonCommandCompiler extends ExecuteFileCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        if (analysis.startsWith(command, "daemon", 0, false)) {
            String filepath = analysis.trim(command.substring("daemon".length()), 0, 1); // 脚本文件路径
            return new DaemonCommand(this, command, filepath);
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr033", command));
        }
    }

}
