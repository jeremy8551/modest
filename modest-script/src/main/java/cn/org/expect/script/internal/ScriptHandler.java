package cn.org.expect.script.internal;

import java.sql.SQLException;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptExpression;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * declare global (exit | continue) handler for ( exception | exitcode != 0 | sqlstate == '02501' | errorcode -803 ) begin .. end 语句
 */
public class ScriptHandler {
    private final static Log log = LogFactory.getLog(ScriptHandler.class);

    /** 异常处理逻辑的执行条件（已删除空白字符且字符串转为小写字符） */
    private final String key;

    /** 异常处理逻辑的执行代码 */
    private final CommandList body;

    /** true 表示执行完异常处理逻辑后退出脚本引擎 false表示执行完异常处理逻辑后继续向下执行 */
    private final boolean exit;

    /** 异常处理逻辑的匹配条件，exception | exitcode != 0 | sqlstate == -803 | errorcode == -803 */
    private final String condition;

    /** 正在运行的脚本命令 */
    protected UniversalScriptCommand command;

    /** 异常处理逻辑语句 */
    private final String script;

    /**
     * 初始化
     *
     * @param exitOrContinue exit 或 continue
     * @param condition      异常处理逻辑的执行条件: exception | exitcode != 0 | sqlstate == '02501' | errorcode == -803
     * @param body           异常处理逻辑的方法体
     * @param script         异常处理逻辑语句
     */
    public ScriptHandler(String exitOrContinue, String condition, CommandList body, String script) {
        this.body = body;
        Ensure.existsIgnoreCase(exitOrContinue, "continue", "exit");
        this.exit = "exit".equalsIgnoreCase(exitOrContinue);
        this.condition = StringUtils.trimBlank(condition);
        this.key = ScriptHandler.toKey(this.condition);
        this.script = script;
    }

    /**
     * 返回处理逻辑中的命令集合
     *
     * @return 处理逻辑体
     */
    public CommandList getList() {
        return body;
    }

    public ScriptHandler clone() {
        CommandList list = this.body.clone();
        String exit = this.exit ? "exit" : "continue";
        return new ScriptHandler(exit, this.condition, list, this.script);
    }

    /**
     * 删除异常处理逻辑执行条件中的空白字符
     *
     * @param condition 异常处理逻辑执行条件
     * @return 字符串
     */
    public static String toKey(String condition) {
        return StringUtils.removeBlank(condition).toLowerCase();
    }

    /**
     * 返回 error 或 step 或 echo 或 handle
     *
     * @return 处理逻辑名
     */
    public String getName() {
        return this.body.getName();
    }

    /**
     * 返回异常处理逻辑的执行条件
     *
     * @return 执行条件
     */
    public String getCondition() {
        return condition;
    }

    /**
     * 异常处理逻辑的匹配规则（无空白字符）
     *
     * @return 匹配规则
     */
    public String getKey() {
        return key;
    }

    /**
     * true表示执行逻辑命令集合后退出脚本<br>
     * false意味着继续执行
     *
     * @return 返回true表示执行逻辑命令集合后退出脚本
     */
    public boolean isReturnExit() {
        return exit;
    }

    /**
     * 执行退出脚本引擎的处理逻辑代码
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param exitcode    脚本引擎的退出值
     * @return 返回true表示匹配异常错误处理逻辑
     */
    public boolean executeExitcode(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Integer exitcode) {
        if (this.condition == null) {
            return false;
        }

        // 保存内置变量
        session.addVariable(UniversalScriptVariable.VARNAME_EXITCODE, exitcode);

        // 判断是否满足异常处理逻辑的执行条件
        if (new UniversalScriptExpression(session, context, stdout, stderr, this.condition).booleanValue()) {
            try {
                this.execute(session, context, stdout, stderr, forceStdout, this.body, new String[]{"handler"});
            } catch (Throwable e) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message055", this.toString()), e);
            }
            return true;
        }

        return false;
    }

    /**
     * 判断是否匹配异常错误处理逻辑 <br>
     * <br>
     * exception | sqlstate == '02501' | errorcode == -803 statement
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param script      发生错误的脚本语句
     * @param exception   异常信息
     * @return 返回true表示匹配异常错误处理逻辑
     */
    public boolean executeException(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, String script, Throwable exception) {
        if (exception == null || this.condition == null) {
            return false;
        }

        try {
            // 保存内置变量
            session.addVariable(UniversalScriptVariable.VARNAME_ERRORSCRIPT, script);
            session.addVariable(UniversalScriptVariable.VARNAME_EXCEPTION, context.getEngine().getFormatter().format(exception));

            if (UniversalScriptVariable.VARNAME_EXCEPTION.equalsIgnoreCase(this.condition)) {
                this.execute(session, context, stdout, stderr, forceStdout, this.body, new String[]{"handler"});
                return true;
            }

            Throwable cause = exception;
            while (cause != null) {
                if (cause instanceof SQLException) { // 如果是数据库错误
                    SQLException sqlExp = (SQLException) cause;
                    while (sqlExp != null) {
                        String sqlstate = sqlExp.getSQLState();
                        String errorcode = String.valueOf(sqlExp.getErrorCode()); // 如：-803 表示主键冲突

                        // 保存内置变量
                        session.addVariable(UniversalScriptVariable.VARNAME_SQLSTATE, StringUtils.isInt(sqlstate) ? Integer.parseInt(sqlstate) : sqlstate);
                        session.addVariable(UniversalScriptVariable.VARNAME_ERRORCODE, StringUtils.isInt(errorcode) ? Integer.parseInt(errorcode) : errorcode);

                        // 判断是否满足异常处理逻辑的执行条件
                        if (new UniversalScriptExpression(session, context, stdout, stderr, this.condition).booleanValue()) {
                            this.execute(session, context, stdout, stderr, forceStdout, this.body, new String[]{"handler"});
                            return true;
                        }

                        sqlExp = sqlExp.getNextException();
                    }
                }

                cause = cause.getCause();
            }
        } catch (Throwable e1) {
            try {
                stderr.println(script, exception);
            } catch (Throwable e2) {
                log.error(script, exception);
            }

            try {
                stderr.println(script, e1);
            } catch (Throwable e3) {
                log.error(script, e1);
            }
        }

        return false;
    }

    protected int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, CommandList body, String[] args) throws Exception {
        try {
            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            }

            session.setFunctionParameter(args);
            for (int i = 0; !session.isTerminate() && i < body.size(); i++) {
                UniversalScriptCommand command = body.get(i);
                if (command == null) {
                    continue;
                } else {
                    this.command = command;
                }

                int exitcode = command.execute(session, context, stdout, stderr, forceStdout);
                if (exitcode != 0) {
                    return exitcode;
                }

                if (command instanceof LoopCommandKind) {
                    LoopCommandKind cmd = (LoopCommandKind) command;
                    int type = cmd.kind();
                    if (type == LoopCommandKind.EXIT_COMMAND) { // Exit script
                        return exitcode;
                    } else if (type == LoopCommandKind.RETURN_COMMAND) { // Exit method
                        return exitcode;
                    } else if (type == LoopCommandKind.BREAK_COMMAND) { // break
                        throw new UnsupportedOperationException(ResourcesUtils.getMessage("script.stderr.message028", this.script));
                    } else if (type == LoopCommandKind.CONTINUE_COMMAND) { // continue
                        throw new UnsupportedOperationException(ResourcesUtils.getMessage("script.stderr.message029", this.script));
                    }
                }
            }

            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            } else {
                return 0;
            }
        } finally {
            session.removeFunctionParameter();
            this.command = null;
        }
    }

    /**
     * 清空所有信息
     */
    public void clear() {
        this.body.clear();
//		this.parent = null;
    }

    public String toString(boolean global) {
        StringBuilder buf = new StringBuilder();
        buf.append("declare");
        buf.append(global ? " global" : "");
        buf.append(this.exit ? " exit" : " continue");
        buf.append(" handler for ");
        buf.append(this.condition);
        buf.append(" begin .. end");
        return buf.toString();
    }
}
