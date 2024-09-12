package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.command.feature.DefaultCommandSupported;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

@ScriptCommand(name = "quiet", keywords = {"quiet"})
public class QuietCommandCompiler extends AbstractTraceCommandCompiler implements DefaultCommandSupported {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        String word = in.readSingleWord();
        if (word.endsWith(String.valueOf(analysis.getToken()))) { // 单词右侧不能有语句分隔符
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr078", word));
        } else {
            Ensure.exists("quiet", word);
            return word;
        }
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws Exception {
        UniversalScriptCommand subcommand = parser.read();
        String script = command + " " + subcommand.getScript();
        return new QuietCommand(this, script, subcommand);
    }

}
