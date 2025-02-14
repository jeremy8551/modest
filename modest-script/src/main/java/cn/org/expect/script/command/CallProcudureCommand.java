package cn.org.expect.script.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.DatabaseProcedureParameter;
import cn.org.expect.database.DatabaseProcedureParameterList;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptChecker;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 执行数据库存储过程，call schema.procudure(?, 'test');
 */
public class CallProcudureCommand extends AbstractTraceCommand implements JumpCommandSupported, NohupCommandSupported {

    /** SQL语句 */
    private String sql;

    /** 数据库操作类 */
    private JdbcDao dao;

    public CallProcudureCommand(UniversalCommandCompiler compiler, String command, String sql) {
        super(compiler, command);
        this.sql = sql;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        ScriptDataSource dataSource = ScriptDataSource.get(context);
        this.dao = dataSource.getDao();
        try {
            UniversalScriptAnalysis analysis = session.getAnalysis();
            String sql = analysis.replaceSQLVariable(session, context, this.sql);
            if (!this.dao.isConnected()) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message057", sql));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            if (session.isEchoEnable() || forceStdout) {
                stdout.println(sql);
            }

            UniversalScriptChecker checker = context.getEngine().getChecker();
            DatabaseProcedure obj = this.dao.callProcedure(sql);
            DatabaseProcedureParameterList parameters = obj.getParameters();
            for (int i = 0, size = parameters.size(); i < size; i++) {
                DatabaseProcedureParameter parameter = parameters.get(i);
                if (parameter.isOutMode() && checker.isVariableName(parameter.getExpression())) {
                    List<String> list = StringUtils.splitVariable(parameter.getExpression(), new ArrayList<String>());
                    if (list.size() != 1) {
                        stderr.println(ResourcesUtils.getMessage("script.stderr.message038", sql, parameter.getPlaceholder(), parameter.getExpression()));
                        return UniversalScriptCommand.COMMAND_ERROR;
                    } else {
                        String variableName = list.get(0);
                        context.addLocalVariable(variableName, parameter.getValue()); // 保存存储过程输出变量
                    }
                }
            }

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
