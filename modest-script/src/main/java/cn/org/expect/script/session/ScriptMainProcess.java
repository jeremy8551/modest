package cn.org.expect.script.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.CommandResultSet;
import cn.org.expect.util.StringUtils;

/**
 * 主线程
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptMainProcess {

    /** 会话创建时间 */
    private final Date create;

    /** 最后执行的命令的返回值 */
    private Integer exitcode;

    /** 最近一次运行失败的命令 */
    private UniversalScriptCommand failCommand;

    /** 编号与命令的映射关系 */
    private final LinkedHashMap<String, UniversalScriptCommand> cache;

    /**
     * 初始化
     */
    public ScriptMainProcess() {
        this.cache = new LinkedHashMap<String, UniversalScriptCommand>();
        this.create = new Date();
    }

    /**
     * 返回最后一个命令的返回值
     *
     * @return 返回值
     */
    public Integer getExitcode() {
        return this.exitcode;
    }

    /**
     * 执行命令
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param command     脚本命令
     * @return 脚本命令执行结果集
     * @throws Exception 执行脚本命令发生错误
     */
    public UniversalCommandResultSet execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command) throws Exception {
        String key = StringUtils.toRandomUUID();
        try {
            this.cache.put(key, command);
            boolean can = context.getListenerList().beforeCommand(session, context, stdout, stderr, command); // 脚本命令执行前执行的逻辑代码
            CommandResultSet resultSet = new CommandResultSet();
            try {
                int exitcode = 0;
                if (can) {
                    exitcode = command.execute(session, context, stdout, stderr, forceStdout);
                }

                resultSet.setExitcode(exitcode);
                this.exitcode = exitcode;
                if (exitcode != 0) {
                    resultSet.setExitSession(true);
                    this.failCommand = command;
                }

                context.getListenerList().afterCommand(session, context, stdout, stderr, forceStdout, command, resultSet); // 脚本命令执行完毕后执行的逻辑代码
            } catch (Exception e) { // 脚本命令执行报错后执行的逻辑代码
                this.failCommand = command;
                context.getListenerList().catchCommand(session, context, stdout, stderr, forceStdout, command, resultSet, e);
            }

            return resultSet;
        } finally {
            this.cache.remove(key);
        }
    }

    /**
     * 终止所有命令
     *
     * @throws Exception 终止命令发生错误
     */
    public void terminate() throws Exception {
        Set<String> keys = this.cache.keySet();
        for (String key : keys) {
            UniversalScriptCommand command = this.cache.get(key);
            if (command != null) {
                command.terminate();
            }
        }
    }

    /**
     * 返回所有正在执行命令
     *
     * @return 命令集合
     */
    public List<UniversalScriptCommand> getExecutingCommandList() {
        return new ArrayList<UniversalScriptCommand>(this.cache.values());
    }

    /**
     * 主线程创建时间
     *
     * @return 创建时间
     */
    public Date getCreateTime() {
        return this.create;
    }

    /**
     * 返回最近一次执行失败的命令
     *
     * @return 执行失败的命令
     */
    public UniversalScriptCommand getErrorCommand() {
        return this.failCommand;
    }

    /**
     * 返回最后一次执行失败的语句
     *
     * @return 脚本语句
     */
    public String getErrorScript() {
        return this.failCommand == null ? null : this.failCommand.getScript();
    }
}
