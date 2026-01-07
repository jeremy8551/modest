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

    private volatile JdbcDao dao;

    public DDLCommand(UniversalCommandCompiler compiler, String command, String tableName, String schema) {
        super(compiler, command);
        this.tableName = tableName;
        this.schema = schema;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(analysis.replaceShellVariable(session, context, this.command, true, true));
        }

        this.dao = ScriptDataSource.get(context).getDao();
        String catalog = this.dao.getCatalog();
        String schema = analysis.replaceShellVariable(session, context, analysis.unQuotation(this.schema), true, !analysis.containsQuotation(this.schema));
        String tableName = analysis.replaceShellVariable(session, context, analysis.unQuotation(this.tableName), true, !analysis.containsQuotation(this.tableName));

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
        super.terminate();
        if (this.dao != null) {
            this.dao.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
