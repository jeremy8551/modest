package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.internal.CallbackMap;
import cn.org.expect.util.StringUtils;

/**
 * 删除命令对应的回调函数 <br>
 * undeclare global command callback for exit | quit | echo | step[;]
 */
public class UndeclareCallbackCommand extends AbstractGlobalCommand implements LoopCommandSupported {

    /** 回调函数对应的命令表达式 */
    private Class<? extends UniversalCommandCompiler> cls;

    public UndeclareCallbackCommand(UniversalCommandCompiler compiler, String command, Class<? extends UniversalCommandCompiler> cls, boolean global) {
        super(compiler, command);
        this.cls = cls;
        this.setGlobal(global);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        if (print) {
            stdout.println(StringUtils.escapeLineSeparator(this.command));
        }

        CallbackMap.get(context, this.isGlobal()).remove(this.cls);
        return 0;
    }

    public boolean enableLoop() {
        return false;
    }
}
