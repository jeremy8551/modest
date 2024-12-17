package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.annotation.ScriptCommand;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.ResourcesUtils;

@ScriptCommand(name = {"."}, keywords = {UniversalScriptVariable.SESSION_VARNAME_SCRIPTNAME, UniversalScriptVariable.SESSION_VARNAME_SCRIPTFILE, UniversalScriptVariable.SESSION_VARNAME_LINESEPARATOR})
public class ExecuteFileCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        if (command.startsWith(".")) {
            String filepath = analysis.trim(command.substring(1), 0, 1); // 脚本文件路径
            return new ExecuteFileCommand(this, orginalScript, filepath);
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr033", command));
        }
    }

}
