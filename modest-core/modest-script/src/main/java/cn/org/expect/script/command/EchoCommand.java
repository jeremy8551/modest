package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.CallbackCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.ResourcesUtils;

/**
 * 输出信息 <br>
 * 打开输出： echo on <br>
 * 关闭输出： echo off <br>
 * 不输出回车换行： echo -n message
 */
public class EchoCommand extends AbstractTraceCommand implements CallbackCommandSupported, NohupCommandSupported {

    /** true 表示可以输出信息 */
    private boolean turnOn;

    /** true 表示不可以输出信息 */
    private boolean turnOff;

    /** 输出内容 */
    private String message;

    /** true 表示输出信息后不追加行间分隔符 */
    private boolean nonewline;

    public EchoCommand(UniversalCommandCompiler compiler, String command, String message, boolean nonewline) {
        super(compiler, command);
        this.message = message;
        this.nonewline = nonewline;
    }

    public EchoCommand(UniversalCommandCompiler compiler, String command, boolean on) {
        super(compiler, command);
        this.turnOn = on ? true : false;
        this.turnOff = on ? false : true;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (this.turnOn) {
            session.setEchoEnabled(true);
            stdout.println(ResourcesUtils.getMessage("script.message.stdout022"));
            return 0;
        } else if (this.turnOff) {
            session.setEchoEnabled(false);
            stdout.println(ResourcesUtils.getMessage("script.message.stdout023"));
            return 0;
        }

        if (session.isEchoEnable() || forceStdout) {
            UniversalScriptAnalysis analysis = session.getAnalysis();
            String message = analysis.replaceShellVariable(session, context, this.message, true, true, true, true);
            if (this.nonewline) {
                stdout.print(message);
            } else {
                stdout.println(message);
            }
        }
        return 0;
    }

    public void terminate() throws Exception {
    }

    public boolean enableNohup() {
        return true;
    }

    public String[] getArguments() {
        return new String[]{"echo", this.message};
    }

}
