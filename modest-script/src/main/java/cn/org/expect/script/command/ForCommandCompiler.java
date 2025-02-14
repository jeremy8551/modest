package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.internal.CommandList;

@EasyCommandCompiler(name = "for", keywords = {"for", "loop", "end"})
public class ForCommandCompiler extends AbstractCommandCompiler {

    public final static String REGEX = "^(?i)for\\s+\\S+\\s+in\\s*.*";

    private final Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readPieceofScript("loop", "end loop");
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("for");
        String name = analysis.trim(it.next(), 0, 0);
        it.assertNext("in");
        String collection = it.readUntil("loop");
        it.assertLast("loop");
        it.assertLast("end");
        String script = it.readOther();
        List<UniversalScriptCommand> list = parser.read(script);
        CommandList cmdlist = new CommandList("for", list, command);
        return new ForCommand(this, command, name, collection, cmdlist);
    }
}
