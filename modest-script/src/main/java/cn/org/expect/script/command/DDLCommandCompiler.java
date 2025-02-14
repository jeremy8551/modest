package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.database.Jdbc;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "ddl")
public class DDLCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        String[] array = StringUtils.splitByBlank(StringUtils.trimBlank(command));
        if (array.length > 1) {
            String schema = Jdbc.getSchema(array[1]);
            String tableName = Jdbc.removeSchema(array[1]);
            return new DDLCommand(this, orginalScript, tableName, schema);
        }

        throw new UniversalScriptException("script.stderr.message134", command);
    }
}
