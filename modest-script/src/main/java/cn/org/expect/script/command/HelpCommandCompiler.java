package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.StringUtils;

@ScriptCommand(name = {"help", "man"}, keywords = {"help", "man"})
public class HelpCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        String str = StringUtils.ltrimBlank(command);
        int index = StringUtils.indexOfBlank(str, 0, -1);
        String parameter = index == -1 ? "" : StringUtils.trimBlank(str.substring(index));
        return new HelpCommand(this, orginalScript, parameter);
    }

}
