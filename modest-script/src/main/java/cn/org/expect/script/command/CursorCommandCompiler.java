package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.internal.CommandList;

@EasyCommandCompiler(name = "cursor", keywords = {"cursor"})
public class CursorCommandCompiler extends AbstractCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readPieceofScript("loop", "end loop");
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("cursor");
        String name = it.next();
        it.assertNext("loop");
        it.assertLast("loop");
        it.assertLast("end");
        String body = it.readOther();
        List<UniversalScriptCommand> commands = parser.read(body);
        for (UniversalScriptCommand cmd : commands) {
            if ((cmd instanceof LoopCommandSupported) && !((LoopCommandSupported) cmd).enableLoop()) {
                throw new UniversalScriptException("script.stderr.message027", command, cmd.getScript()); // 在语句中不能使用的语句
            }
        }

        CommandList list = new CommandList(name, commands, command);
        return new CursorCommand(this, command, name, list);
    }
}
