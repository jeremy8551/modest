package cn.org.expect.script.command;

import java.util.List;

import cn.org.expect.database.DatabaseException;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.internal.ScriptStatement;
import cn.org.expect.script.internal.StatementMap;
import cn.org.expect.util.ResourcesUtils;

/**
 * 将变量更新到数据库表中 <Br>
 * <p>
 * FETCH tmp_ywdate, tmp_orgcode, tmp_finishcode INSERT statmentName;
 */
public class FetchStatementCommand extends AbstractCommand implements JumpCommandSupported {

    /** 批处理名 */
    private String name;

    /** 变量名数组 */
    private List<String> variableNames;

    public FetchStatementCommand(UniversalCommandCompiler compiler, String command, String name, List<String> variableNames) {
        super(compiler, command);
        this.name = name;
        this.variableNames = variableNames;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (!context.getEngine().getChecker().isVariableName(this.name)) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr088", this.command, this.name));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        StatementMap map = StatementMap.get(context);
        if (!map.contains(this.name)) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr003", this.name));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        // 变量个数 与 SQL语句中参数一致
        ScriptStatement statement = map.get(this.name);
        int size = statement.getParameterCount();
        if (size != this.variableNames.size()) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr138", this.command, size));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        try {
            for (int i = 0; i < size; i++) {
                String variableName = this.variableNames.get(i); // variable name
                Object value = context.getVariable(variableName); // variable value
                statement.setParameter(i, value);
            }
            statement.executeBatch();
            return 0;
        } catch (Exception e) {
            throw new DatabaseException(this.command, e);
        }
    }

    public void terminate() throws Exception {
    }

    public boolean enableJump() {
        return true;
    }

}
