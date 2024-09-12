package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.expression.CommandExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptUsage;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

@ScriptCommand(name = "ps", keywords = {"ps"})
public class PSCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        CommandExpression expr = new CommandExpression(analysis, "ps -s {0}", command);
        return new PSCommand(this, orginalScript, expr.containsOption("-s") ? 1 : 0);
    }

    public void usage(UniversalScriptContext context, UniversalScriptStdout out) {
        String charsetName = context.getCharsetName();
        String[] cmd = StringUtils.split(ResourcesUtils.getMessage("script.message.stdout048"), ',');
        String[] ses = StringUtils.split(ResourcesUtils.getMessage("script.message.stdout049"), ',');
        int length = Math.max(StringUtils.width(cmd, charsetName), StringUtils.width(ses, charsetName));
        out.println(new ScriptUsage(this.getClass() //
                , StringUtils.left(cmd[0], length, charsetName, ' ') //
                , StringUtils.left(cmd[1], length, charsetName, ' ') //
                , StringUtils.left(cmd[2], length, charsetName, ' ') //
                , StringUtils.left(cmd[3], length, charsetName, ' ') //
                , StringUtils.left(cmd[4], length, charsetName, ' ') //
                , StringUtils.left(cmd[5], length, charsetName, ' ') //
                , StringUtils.left(cmd[6], length, charsetName, ' ') //
                , StringUtils.left(cmd[7], length, charsetName, ' ') //
                , StringUtils.left(ses[0], length, charsetName, ' ') //
                , StringUtils.left(ses[1], length, charsetName, ' ') //
                , StringUtils.left(ses[2], length, charsetName, ' ') //
                , StringUtils.left(ses[3], length, charsetName, ' ') //
                , StringUtils.left("*", length, charsetName, ' ') //
        ));
    }

}
