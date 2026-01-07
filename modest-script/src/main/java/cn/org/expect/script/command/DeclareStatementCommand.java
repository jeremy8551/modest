package cn.org.expect.script.command;

import cn.org.expect.database.JdbcDao;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptChecker;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.script.internal.ScriptStatement;
import cn.org.expect.script.internal.StatementMap;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

/**
 * 建立数据库批处理程序 <br>
 * <br>
 * DECLARE name Statement by 1000 batch WITH insert into v1_test (f1, f2, f3) values (?,?, ?);
 */
public class DeclareStatementCommand extends AbstractCommand {

    /** 批处理名字 */
    private String name;

    /** SQL语句 */
    private String sql;

    /** 批量提交记录数 */
    private String batchRecords;

    /** 数据库操作类 */
    private volatile JdbcDao dao;

    public DeclareStatementCommand(UniversalCommandCompiler compiler, String command, String name, String sql, String batchRecords) {
        super(compiler, command);
        this.name = name;
        this.sql = sql;
        this.batchRecords = batchRecords;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String sql = analysis.replaceSQLVariable(session, context, this.sql);
        ScriptDataSource dataSource = ScriptDataSource.get(context);
        this.dao = dataSource.getDao();
        try {
            if (!this.dao.isConnected()) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message057", this.command));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            String name = analysis.replaceShellVariable(session, context, this.name, true, true);
            UniversalScriptChecker checker = context.getEngine().getChecker();
            if (!checker.checkVariableName(name) || !checker.checkDatabaseKeyword(name)) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message064", this.command, name));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            if (session.isEchoEnable() || forceStdout) {
                stdout.println("declare " + name + " statement with " + sql);
            }

            int batch = Ensure.isInt(analysis.replaceShellVariable(session, context, this.batchRecords, true, true));
            ScriptStatement statement = new ScriptStatement(this.dao, context.getEngine().getFormatter(), batch, name, sql);
            StatementMap.get(context).put(name, statement);
            return 0;
        } finally {
            this.dao = null;
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.dao != null) {
            Ensure.isTrue(this.dao.terminate());
        }
    }
}
