package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.DefaultCommandSupported;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "quiet", keywords = {"quiet"})
public class QuietCommandCompiler extends AbstractTraceCommandCompiler implements DefaultCommandSupported {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws Exception {
        String subcommand = StringUtils.ltrimBlank(command.substring("quiet".length()));
        List<UniversalScriptCommand> list = parser.read(subcommand);
        Ensure.equals(1, list.size());
        return new QuietCommand(this, command, list.get(0));
    }
}
