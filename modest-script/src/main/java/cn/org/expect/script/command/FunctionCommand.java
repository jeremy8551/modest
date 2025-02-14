package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.internal.FunctionSet;
import cn.org.expect.util.ResourcesUtils;

/**
 * 建立用户自定义方法 <br>
 * function name() { .... }
 */
public class FunctionCommand extends AbstractCommand implements LoopCommandSupported, WithBodyCommandSupported {

    /** 用户自定义方法体 */
    protected CommandList body;

    public FunctionCommand(UniversalCommandCompiler compiler, String command, CommandList body) {
        super(compiler, command);
        this.body = body;
        this.body.setOwner(this);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        FunctionSet set = FunctionSet.get(context, false);
        String functionName = this.body.getName();
        if (this.body.isEmpty() && set.contains(functionName)) { // 删除方法-当方法体为空时
            set.remove(functionName);
        } else { // 添加一个用户自定义方法
            CommandList old = set.add(this.body);
            boolean print = session.isEchoEnable() || forceStdout;
            if (old != null && print) {
                stdout.println(ResourcesUtils.getMessage("script.stdout.message009", session.getScriptName(), old.getName()));
            }
        }
        return 0;
    }

    public boolean enableLoop() {
        return false;
    }
}
