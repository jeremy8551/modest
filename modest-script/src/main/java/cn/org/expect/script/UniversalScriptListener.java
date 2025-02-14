package cn.org.expect.script;

import java.io.Reader;

public interface UniversalScriptListener {

    /**
     * 执行脚本语句前，运行的业务逻辑
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param in          脚本语句的输入流
     * @throws Exception 发生错误
     */
    void startEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception;

    /**
     * 执行脚本语句抛出异常时，运行的业务逻辑
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param command     脚本命令
     * @param result      脚本命令执行结果
     * @param e           执行脚本命令时抛出了异常信息
     * @return 返回 true 表示执行了业务逻辑，false 表示无业务逻辑
     * @throws Exception 发生错误
     */
    boolean catchEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception;

    /**
     * 执行脚本语句后，运行的业务逻辑
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param command     脚本命令
     * @param result      脚本命令执行结果
     * @throws Exception 发生错误
     */
    void exitEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception;

    /**
     * 执行脚本命令前，运行的业务逻辑
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param stdout  标准信息输出接口
     * @param stderr  错误信息输出接口
     * @param command 脚本命令
     * @return 返回 true 表示可以执行 {@linkplain UniversalScriptCommand#execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean)} 方法 <br>
     * 返回 false 表示跳过 {@linkplain UniversalScriptCommand#execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean)} 方法执行下一个命令
     * @throws Exception 发生错误
     */
    boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) throws Exception;

    /**
     * 执行脚本命令后，运行的业务逻辑
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param command     脚本命令
     * @param result      脚本命令执行结果
     * @throws Exception 发生错误
     */
    void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception;

    /**
     * 脚本命令抛出异常时，运行的业务逻辑
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param command     脚本命令
     * @param result      脚本命令执行结果
     * @param e           执行脚本命令时抛出了异常信息
     * @return 返回 true 表示执行了业务逻辑，false 表示无业务逻辑
     * @throws Exception 发生错误
     */
    boolean catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception;
}
