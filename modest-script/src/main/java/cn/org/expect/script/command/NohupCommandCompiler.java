package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.NohupCommandSupported;

@EasyCommandCompiler(name = "nohup")
public class NohupCommandCompiler extends AbstractCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        String str = analysis.trim(command.substring("nohup".length()), 0, 1);
        String script;
        if (str.endsWith("&")) {
            script = str.substring(0, str.length() - 1);
        } else {
            script = str;
        }

        List<UniversalScriptCommand> list = parser.read(script);
        if (list.isEmpty()) { // nohup 命令中语句不能为空
            throw new UniversalScriptException("script.stderr.message021", command);
        }

        if (list.size() != 1) { // nohup 命令只能并发执行一个命令
            throw new UniversalScriptException("script.stderr.message019", command);
        }

        UniversalScriptCommand subcommand = list.get(0);
        if ((subcommand instanceof NohupCommandSupported) && ((NohupCommandSupported) subcommand).enableNohup()) { // 检查是否支持后台运行
            return new NohupCommand(this, command, subcommand);
        } else {
            throw new UniversalScriptException("script.stderr.message020", command, subcommand.getScript());
        }
    }
}
