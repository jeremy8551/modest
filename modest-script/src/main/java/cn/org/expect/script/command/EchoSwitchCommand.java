package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;

/**
 * 输出信息 <br>
 * 打开输出： echo on <br>
 * 关闭输出： echo off <br>
 * 不输出回车换行： echo -n message
 */
public class EchoSwitchCommand extends AbstractTraceCommand implements NohupCommandSupported {

    /** true 表示可以输出信息 */
    private boolean on;

    public EchoSwitchCommand(UniversalCommandCompiler compiler, String command, boolean on) {
        super(compiler, command);
        this.on = on;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        // 不需要使用 session.isEchoEnable() || forceStdout) 来控制标准输出
        if (this.on) {
            stdout.println("echo on");
            session.setEchoEnabled(true);
            return 0;
        } else {
            stdout.println("echo off");
            session.setEchoEnabled(false);
            return 0;
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
