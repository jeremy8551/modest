package cn.org.expect.script.command;

import java.io.Reader;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptListener;
import cn.org.expect.script.UniversalScriptListenerList;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.CallbackCommandSupported;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.CallbackMap;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.util.StringUtils;

/**
 * 定义一个脚本命令的回调函数
 */
public class CallbackCommand extends AbstractGlobalCommand implements LoopCommandSupported, WithBodyCommandSupported {

    public final static String NAME = "CallbackCommand";

    /** 异常处理逻辑的执行方法 */
    private final CommandList body;

    /** 回调函数对应的脚本命令信息 */
    private final Class<?> type;

    public CallbackCommand(UniversalCommandCompiler compiler, String command, Class<?> type, CommandList body, boolean global) {
        super(compiler, command);
        this.type = type;
        this.body = body;
        this.body.setOwner(this);
        this.setGlobal(global);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(StringUtils.escapeLineSeparator(this.command));
        }

        CallbackMap.get(context, this.isGlobal()).add(this.type, this.body);
        UniversalScriptListenerList list = context.getListenerList();
        if (!list.contains(CallbackListener.class)) {
            list.add(new CallbackListener());
        }
        return 0;
    }

    public boolean enableLoop() {
        return false;
    }

    static class CallbackListener implements UniversalScriptListener {

        public CallbackListener() {
        }

        public void startEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
        }

        public boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) throws Exception {
            return true;
        }

        public void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception {
            if (result.getExitcode() != 0) { // 如果上一个命令返回值是非0
                return;
            } else if (command instanceof CallbackCommandSupported) {
                CallbackCommandSupported supported = (CallbackCommandSupported) command;
                String[] args = supported.getArguments(); // 命令的参数数组
                Class<? extends UniversalCommandCompiler> type = command.getCompiler().getClass();

                // 执行局部方法
                CallbackMap local = CallbackMap.get(context, false); // 局部
                local.executeCallback(session, context, stdout, stderr, forceStdout, type, args);

                // 执行全局方法
                CallbackMap global = CallbackMap.get(context, true); // 全局
                global.executeCallback(session, context, stdout, stderr, forceStdout, type, args);
            }
        }

        public boolean catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception {
            return false;
        }

        public boolean catchEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception {
            return false;
        }

        public void exitEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) {
        }
    }
}
