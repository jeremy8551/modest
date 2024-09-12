package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.internal.ErrorHandlerMap;
import cn.org.expect.script.internal.ExitHandlerMap;
import cn.org.expect.util.StringUtils;

/**
 * 删除异常错误处理逻辑 <br>
 * undeclare handler for ( exception | exitcode == 0 | sqlstate == 120 | sqlcode == -803 ) ;
 */
public class UndeclareHandlerCommand extends AbstractGlobalCommand implements LoopCommandSupported {

    /** 异常处理逻辑的执行条件：exception | exitcode == 0 | sqlstate == 120 | sqlcode == -803 */
    private String condition;

    /** true表示退出处理逻辑 false表示错误处理逻辑 */
    private boolean isExitHandler;

    public UndeclareHandlerCommand(UniversalCommandCompiler compiler, String command, String condition, boolean isExitHandler, boolean global) {
        super(compiler, command);
        this.condition = condition;
        this.isExitHandler = isExitHandler;
        this.setGlobal(global);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        if (print) {
            stdout.println(StringUtils.escapeLineSeparator(this.command));
        }

        String condition = session.getAnalysis().replaceVariable(session, context, this.condition, false);
        if (this.isExitHandler) {
            ExitHandlerMap map = ExitHandlerMap.get(context, this.isGlobal());
            map.remove(condition);
            return 0;
        } else {
            ErrorHandlerMap map = ErrorHandlerMap.get(context, this.isGlobal());
            map.remove(condition);
            return 0;
        }
    }

    public void terminate() throws Exception {
    }

    public boolean enableLoop() {
        return false;
    }

}