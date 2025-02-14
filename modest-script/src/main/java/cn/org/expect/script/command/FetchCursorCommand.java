package cn.org.expect.script.command;

import java.sql.ResultSet;
import java.util.List;

import cn.org.expect.database.Jdbc;
import cn.org.expect.database.JdbcQueryStatement;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.CursorMap;
import cn.org.expect.util.ResourcesUtils;

/**
 * 从游标中读取信息到变量中 <br>
 * <p>
 * FETCH cno INTO tmp_ywdate, tmp_orgcode, tmp_finishcode;
 */
public class FetchCursorCommand extends AbstractCommand {

    /** 游标名 */
    private final String name;

    /** 变量名数组 */
    private final List<String> variableNames;

    public FetchCursorCommand(UniversalCommandCompiler compiler, String command, String name, List<String> variableNames) {
        super(compiler, command);
        this.name = name;
        this.variableNames = variableNames;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (!context.getEngine().getChecker().isVariableName(this.name)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message069", this.command, this.name));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        CursorMap map = CursorMap.get(context);
        if (!map.contains(this.name)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message002", this.command, this.name));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        JdbcQueryStatement query = map.get(this.name);
        ResultSet result = query.getResultSet();
        int column = Jdbc.getColumnCount(result);

        int size = this.variableNames.size();
        if (size > column) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message011", this.command, size, column));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        for (int i = 0; i < size; i++) {
            context.addLocalVariable(this.variableNames.get(i), result.getObject(i + 1));
        }
        return 0;
    }
}
