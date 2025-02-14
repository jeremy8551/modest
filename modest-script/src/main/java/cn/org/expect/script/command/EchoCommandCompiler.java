package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "echo", keywords = {"echo"})
public class EchoCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        WordIterator it = analysis.parse(command);
        it.assertNext("echo");

        // echo on
        if (it.isNext("on")) {
            it.assertNext("on");
            it.assertOver();
            return new EchoSwitchCommand(this, command, true);
        }

        // echo off
        if (it.isNext("off")) {
            it.assertNext("off");
            it.assertOver();
            return new EchoSwitchCommand(this, command, false);
        }

        // echo -n "str"
        if (it.isNext("-n")) {
            it.assertNext("-n");
            String message = it.readOther();
            if (analysis.containsQuotation(message)) {
                return new EchoCommand(this, command, message);
            } else {
                throw new UniversalScriptException("script.stderr.message067", command);
            }
        }

        // echo "str" -n
        if (it.isLast("-n")) {
            String message = it.readUntil("-n");
            if (analysis.containsQuotation(message)) {
                return new EchoCommand(this, command, message);
            } else {
                throw new UniversalScriptException("script.stderr.message067", command);
            }
        }

        String message = it.readOther();
        return new EchoLFCommand(this, orginalScript, message);
    }
}
