package cn.org.expect.script.command;

import cn.org.expect.database.JdbcDao;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

/**
 * 执行 SQL 语句
 */
public class SQLCommand extends AbstractCommand implements JumpCommandSupported, NohupCommandSupported {

    /** SQL语句 */
    private final String sql;

    /** 数据库操作类 */
    private JdbcDao dao;

    public SQLCommand(UniversalCommandCompiler compiler, String command, String sql) {
        super(compiler, command);
        this.sql = sql;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        ScriptDataSource dataSource = ScriptDataSource.get(context);
        this.dao = dataSource.getDao();
        try {
            if (!this.dao.isConnected()) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message057", sql));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            String sql = session.getAnalysis().replaceSQLVariable(session, context, this.sql);
            if (session.isEchoEnable() || forceStdout) {
                stdout.println(sql);
            }

            int rows = this.dao.execute(sql, null);
            session.addVariable(UniversalScriptVariable.VARNAME_UPDATEROWS, rows);
            session.putValue(rows);
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

    public boolean enableNohup() {
        return true;
    }

    public boolean enableJump() {
        return true;
    }
}
