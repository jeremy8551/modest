package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptListenerList;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.CommandHandlerListener;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.internal.ProcessExceptionHandlerMap;
import cn.org.expect.script.internal.ProcessExitcodeHandlerMap;
import cn.org.expect.script.internal.ScriptHandler;
import cn.org.expect.util.StringUtils;

/**
 * 建立异常处理逻辑
 * <p>
 * declare [global] (exit | continue) handler for ( exception | exitcode != 0 | sqlstate == '02501' | errorcode -803 ) begin .. end 语句
 */
public class DeclareHandlerCommand extends AbstractGlobalCommand implements LoopCommandSupported, WithBodyCommandSupported {

    /** 异常处理逻辑的执行方法 */
    private CommandList body;

    /** exit 或 continue */
    private String exitOrContinue;

    /** 异常处理逻辑的执行条件：exception | exitcode != 0 | sqlstate == '02501' | errorcode -803 */
    private String condition;

    /** true表示退出处理逻辑 false表示错误处理逻辑 */
    private boolean isExitHandler;

    public DeclareHandlerCommand(UniversalCommandCompiler compiler, String command, CommandList body, String exitOrContinue, String condition, boolean isExitHandler, boolean global) {
        super(compiler, command);
        this.body = body;
        this.body.setOwner(this);
        this.exitOrContinue = exitOrContinue;
        this.condition = condition;
        this.isExitHandler = isExitHandler;
        this.setGlobal(global);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(StringUtils.escapeLineSeparator(this.command));
        }

        boolean global = this.isGlobal();
        ScriptHandler handler = new ScriptHandler(this.exitOrContinue, this.condition, this.body, this.command);
        if (this.isExitHandler) {
            ProcessExitcodeHandlerMap.get(context, global).add(handler);
        } else {
            ProcessExceptionHandlerMap.get(context, global).add(handler);
        }

        // 让异常处理模块与 UniversalScriptListenerList 解耦
        UniversalScriptListenerList list = context.getListenerList();
        if (!list.contains(CommandHandlerListener.class)) {
            list.add(new CommandHandlerListener());
        }
        return 0;
    }

    public boolean enableLoop() {
        return false;
    }
}
