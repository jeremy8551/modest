package cn.org.expect.script.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cn.org.expect.database.JdbcDao;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptExpression;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
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
    private String name;

    /** 变量值 */
    private String value;

    /** 0表示赋值表达式; 1表示查询SQL; 2表示输出所有变量; 3表示删除变量；4表示检查返回值；5表示不检查返回值； */
    private int type;

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
                stderr.println(ResourcesUtils.getMessage("script.message.stderr131", this.command, this.type, "0, 1, 2, 3"));
                return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public void terminate() throws Exception {
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
        if (!session.getAnalysis().isBlankline(value)) {
            object = new UniversalScriptExpression(session, context, stdout, stderr, value).value();
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
        context.removeVariable(this.name, UniversalScriptContext.ENGINE_SCOPE);
        context.removeVariable(this.name, UniversalScriptContext.GLOBAL_SCOPE);
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
        sql = session.getAnalysis().replaceVariable(session, context, sql, false);
        if (!dao.isConnected()) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr065", sql));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        // 执行查询并将结果集保存到变量域
        Object value = dao.queryFirstRowFirstCol(sql);
        Object newvalue = context.getEngine().getFormatter().formatJdbcParameter(session, context, value);
        if (this.isGlobal()) {
            context.addGlobalVariable(this.name, newvalue);
        } else {
            context.addLocalVariable(this.name, newvalue);
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
        Set<String> gks = context.getGlobalVariable().keySet(); // 全局变量名
        Set<String> lks = context.getLocalVariable().keySet(); // 局部变量名
        Set<String> eks = context.getEnvironmentVariable().keySet(); // 环境变量名
        int size = gks.size() + lks.size() + eks.size(); // 变量个数
        HashSet<String> names = new HashSet<String>(size);
        names.addAll(gks);
        names.addAll(lks);
        names.addAll(eks);

        // 排序
        ArrayList<String> list = new ArrayList<String>(size);
        list.addAll(names);
        Collections.sort(list, new StringComparator());

        // 打印
        StringBuilder buf = new StringBuilder(size * 20);
        for (String name : list) {
            if (context.containsGlobalVariable(name)) {
                buf.append(name).append('=').append(context.getGlobalVariable(name)).append(FileUtils.lineSeparator);
            } else if (context.containsLocalVariable(name)) {
                buf.append(name).append('=').append(context.getLocalVariable(name)).append(FileUtils.lineSeparator);
            } else if (context.containEnvironmentVariable(name)) {
                buf.append(name).append('=').append(context.getEnvironmentVariable(name)).append(FileUtils.lineSeparator);
            } else {
                throw new UnsupportedOperationException(name);
            }
        }
        stdout.println(buf);
    }
}
