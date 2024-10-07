package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.database.DatabaseTable;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.StringUtils;

public class DDLCommand extends AbstractTraceCommand implements NohupCommandSupported {

    private String tableName;

    private String schema;

    private JdbcDao dao;

    public DDLCommand(UniversalCommandCompiler compiler, String command, String tableName, String schema) {
        super(compiler, command);
        this.tableName = tableName;
        this.schema = schema;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(analysis.replaceShellVariable(session, context, this.command, true, true, true, false));
        }

        this.dao = ScriptDataSource.get(context).getDao();
        String catalog = this.dao.getCatalog();
        String schema = analysis.replaceShellVariable(session, context, this.schema, true, true, true, false);
        String tableName = analysis.replaceShellVariable(session, context, this.tableName, true, true, true, false);

        if (StringUtils.isBlank(schema)) {
            schema = this.dao.getSchema();
        }

        DatabaseTable table = this.dao.getTable(catalog, schema, tableName);
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(table == null ? "null" : this.dao.toDDL(table));
        }
        return 0;
    }

    public void terminate() throws Exception {
        if (this.dao != null) {
            this.dao.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }

}
