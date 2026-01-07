package cn.org.expect.script.command;

import java.sql.ResultSet;

import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.JdbcQueryStatement;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.internal.CursorMap;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.script.session.ScriptMainProcess;
import cn.org.expect.util.ResourcesUtils;

/**
 * 遍历数据库游标 <br>
 * <p>
 * CURSOR cno loop ... end loop
 */
public class CursorCommand extends AbstractCommand implements WithBodyCommandSupported, LoopCommandKind {

    /** 游标名 */
    private String name;

    /** 遍历游标的循环体 */
    private CommandList body;

    /** 正在运行的脚本命令 */
    protected volatile UniversalScriptCommand command;

    /** 种类编号 */
    protected int type;

    public CursorCommand(UniversalCommandCompiler compiler, String command, String name, CommandList body) {
        super(compiler, command);
        this.name = name;
        this.body = body;
        this.body.setOwner(this);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String name = analysis.replaceShellVariable(session, context, this.name, true, true);
        CursorMap map = CursorMap.get(context);
        if (!map.contains(name)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message002", this.command, name));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        ScriptDataSource dataSource = ScriptDataSource.get(context);
        JdbcDao dao = dataSource.getDao();
        if (dao.isConnected()) {
            return this.execute(session, context, stdout, stderr, forceStdout, this.body);
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message057", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    protected int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, CommandList body) throws Exception {
        try {
            ScriptMainProcess process = session.getMainProcess();
            boolean isbreak = false, iscontinue = false;
            CursorMap map = CursorMap.get(context);
            JdbcQueryStatement statement = map.get(this.name);
            ScriptDataSource dataSource = ScriptDataSource.get(context);
            JdbcDao dao = dataSource.getDao();
            statement.setConnection(dao.getConnection());
            ResultSet resultSet = statement.query();
            while (!session.isTerminate() && resultSet.next()) {
                iscontinue = false;

                for (int i = 0; !session.isTerminate() && i < body.size(); i++) {
                    UniversalScriptCommand command = body.get(i);
                    this.command = command;
                    if (command == null) {
                        continue;
                    }

                    UniversalCommandResultSet result = process.execute(session, context, stdout, stderr, forceStdout, command);
                    int value = result.getExitcode();
                    if (value != 0) {
                        return value;
                    }

                    if (command instanceof LoopCommandKind) {
                        LoopCommandKind cmd = (LoopCommandKind) command;
                        int type = cmd.kind();
                        this.type = cmd.kind();
                        if (type == LoopCommandKind.BREAK_COMMAND) { // break
                            isbreak = true;
                            break;
                        } else if (type == LoopCommandKind.CONTINUE_COMMAND) { // continue
                            iscontinue = true;
                            break;
                        } else if (type == LoopCommandKind.EXIT_COMMAND) { // Exit script
                            return value;
                        } else if (type == LoopCommandKind.RETURN_COMMAND) { // Exit the result set loop
                            return value;
                        }
                    }
                }

                if (isbreak) {
                    break;
                }

                if (iscontinue) {
                    continue;
                }
            }

            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            } else {
                return 0;
            }
        } finally {
            this.command = null;
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.command != null) {
            this.command.terminate();
        }
    }

    public int kind() {
        return this.type;
    }
}
