package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "export", keywords = {"export", "set", "function"})
public class ExportCommandCompiler extends AbstractCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("export");

        // export set name=value
        if (it.isNext("set")) {
            String script = it.readOther(); // set name=value
            SetCommandCompiler scp = new SetCommandCompiler();
            SetCommand subcommand = scp.compile(session, context, parser, analysis, script);
            return new ExportCommand(this, command, subcommand);
        }

        // export function name
        if (it.isNext("function")) {
            it.assertNext("function");
            String name = it.readOther();
            return new ExportCommand(this, command, name);
        }

        throw new UniversalScriptException("script.stderr.message132", command, "export", StringUtils.join(ArrayUtils.as("set", "function"), " || "));
    }
}
