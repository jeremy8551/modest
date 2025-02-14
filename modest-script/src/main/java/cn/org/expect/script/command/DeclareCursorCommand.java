package cn.org.expect.script.command;

import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.JdbcQueryStatement;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptChecker;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.CursorMap;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

/**
 * 建立游标 <br>
 * <br>
 * DECLARE name CURSOR WITH RETURN FOR select * from table ;
 */
public class DeclareCursorCommand extends AbstractCommand implements WithBodyCommandSupported {

    /** 游标名 */
    private final String name;

    /** SQL语句 */
    private final String sql;

    /** 数据库操作类 */
    private JdbcDao dao;

    public DeclareCursorCommand(UniversalCommandCompiler compiler, String command, String name, String sql) {
        super(compiler, command);
        this.name = name;
        this.sql = sql;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        ScriptDataSource dataSource = ScriptDataSource.get(context);
        this.dao = dataSource.getDao();
        try {
            if (!this.dao.isConnected()) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message057", this.command));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            UniversalScriptAnalysis analysis = session.getAnalysis();
            String name = analysis.replaceShellVariable(session, context, this.name, true, false);
            UniversalScriptChecker checker = context.getEngine().getChecker();
            if (!checker.isVariableName(name) || checker.isDatabaseKeyword(name)) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message062", this.command, name));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            String sql = analysis.replaceSQLVariable(session, context, this.sql);
            if (session.isEchoEnable() || forceStdout) {
                stdout.println("declare " + name + " cursor with return for " + sql);
            }

            JdbcQueryStatement cursor = new JdbcQueryStatement(sql);
            CursorMap map = CursorMap.get(context);
            map.put(name, cursor);
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
