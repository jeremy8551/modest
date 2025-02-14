package cn.org.expect.script.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.org.expect.database.JdbcDao;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptExpression;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringComparator;

/**
 * 设置变量
 * set
 * set -E
 * set name=value
 * set name=select * from table
 * set name=
 */
public class SetCommand extends AbstractGlobalCommand {

    /** 变量名 */
    private final String name;

    /** 变量值 */
    private final String value;

    /** 0表示赋值表达式; 1表示查询SQL; 2表示输出所有变量; 3表示删除变量；4表示检查返回值；5表示不检查返回值； */
    private final int type;

    public SetCommand(UniversalCommandCompiler compiler, String command, String name, String value, int type) {
        super(compiler, command);
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        switch (this.type) {
            case 0: // 为变量赋值
                return this.setVariable(session, context, stdout, stderr, this.value);

            case 1: // 使用SQL查询结果为变量赋值
                return this.querySQL(session, context, stdout, stderr, this.value);

            case 2: // 打印所有变量
                if (session.isEchoEnable() || forceStdout) {
                    this.printVariable(session, context, stdout, stderr);
                }
                return 0;

            case 3: // 删除变量
                return this.removeVariable(session, context, stdout, stderr);

            case 4: // 命令执行完毕后，检查返回值
                if (session.isEchoEnable() || forceStdout) {
                    stdout.println("set -e");
                }
                session.setVerifyExitcode(true);
                return 0;

            case 5: // 命令执行完毕后，不检查返回值
                if (session.isEchoEnable() || forceStdout) {
                    stdout.println("set -E");
                }
                session.setVerifyExitcode(false);
                return 0;

            default: // 未知操作
                stderr.println(ResourcesUtils.getMessage("script.stderr.message094", this.command, this.type, "0, 1, 2, 3"));
                return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    /**
     * 为变量赋值
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param stdout  标准信息输出接口
     * @param stderr  错误信息输出接口
     * @param value   变量值
     * @return 命令返回值
     */
    protected int setVariable(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String value) {
        Object object = value;
        if (!session.getAnalysis().isBlank(value)) {
            object = new UniversalScriptExpression(session, context, stdout, stderr, value).value();
        }

        if (log.isTraceEnabled()) {
            log.trace("script.stdout.message049", this.isGlobal() ? "global" : "local", this.name, value, object);
        }

        if (this.isGlobal()) {
            context.addGlobalVariable(this.name, object);
        } else {
            context.addLocalVariable(this.name, object);
        }
        return 0;
    }

    /**
     * 删除变量
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param stdout  标准信息输出接口
     * @param stderr  错误信息输出接口
     * @return 命令返回值
     */
    protected int removeVariable(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr) {
        Object local = context.removeVariable(this.name, UniversalScriptContext.ENGINE_SCOPE);
        if (log.isTraceEnabled()) {
            log.trace("script.stdout.message050", "local", this.name, local);
        }

        Object global = context.removeVariable(this.name, UniversalScriptContext.GLOBAL_SCOPE);
        if (log.isTraceEnabled()) {
            log.trace("script.stdout.message050", "global", this.name, global);
        }
        return 0;
    }

    /**
     * 将SQL查询结果为变量赋值
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param stdout  标准信息输出接口
     * @param stderr  错误信息输出接口
     * @param sql     SQL语句
     * @return 命令返回值
     * @throws Exception 发生错误
     */
    protected int querySQL(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String sql) throws Exception {
        ScriptDataSource dataSource = ScriptDataSource.get(context);
        JdbcDao dao = dataSource.getDao();
        sql = session.getAnalysis().replaceSQLVariable(session, context, sql);
        if (!dao.isConnected()) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message057", sql));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        // 执行查询并将结果集保存到变量域
        Object value = dao.queryFirstRowFirstCol(sql);
        Object newValue = context.getEngine().getFormatter().formatJdbcParameter(session, context, value);

        if (log.isTraceEnabled()) {
            log.trace("script.stdout.message049", this.isGlobal() ? "global" : "local", this.name, sql, newValue);
        }

        if (this.isGlobal()) {
            context.addGlobalVariable(this.name, newValue);
        } else {
            context.addLocalVariable(this.name, newValue);
        }
        return 0;
    }

    /**
     * 打印所有变量
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param stdout  标准信息输出接口
     * @param stderr  错误信息输出接口
     */
    protected void printVariable(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr) {
        List<String> list = new ArrayList<String>(context.getVariableNames());
        Collections.sort(list, new StringComparator());
        StringBuilder buf = new StringBuilder(list.size() * 20);
        for (String name : list) {
            Object variable = context.getVariable(name);
            buf.append(name).append('=').append(variable);
            buf.append(Settings.LINE_SEPARATOR);
        }
        stdout.println(buf);
    }
}
