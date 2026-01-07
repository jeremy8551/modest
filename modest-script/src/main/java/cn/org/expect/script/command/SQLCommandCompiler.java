package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.command.feature.DefaultCommandSupported;

@EasyCommandCompiler(name = {"select", "insert", "update", "delete", "alter", "drop", "create", "merge", "sql", "/*", "/**", "--"})
public class SQLCommandCompiler extends AbstractCommandCompiler implements DefaultCommandSupported {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readMultilineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        String sql = command;
        if (analysis.startsWith(sql, "sql", 0, true)) {
            sql = sql.substring("sql".length());
        }

        return new SQLCommand(this, command, sql);
    }
}
