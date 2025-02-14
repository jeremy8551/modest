package cn.org.expect.script.command;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.org.expect.expression.MillisExpression;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.session.ScriptProcess;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

/**
 * 等待脚本命令执行完毕 <br>
 * <p>
 * wait pid=1 3min|3sec|3hou|3day
 */
public class WaitCommand extends AbstractTraceCommand {

    /** nohup 命令生成的编号 */
    private final String id;

    /** 脚本命令执行的超时时间，单位: 秒 */
    private final String timeout;

    /** 后台线程 */
    private ScriptProcess process;

    public WaitCommand(UniversalCommandCompiler compiler, String command, String id, String timeout) {
        super(compiler, command);
        this.id = id;
        this.timeout = timeout;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        long compileMillis = session.getCompiler().getCompileMillis();
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String pid = analysis.replaceShellVariable(session, context, this.id, true, false);
        String timeoutExpression = analysis.replaceShellVariable(session, context, this.timeout, true, false);
        boolean print = session.isEchoEnable() || forceStdout;

        ScriptProcess process = session.getSubProcess().get(pid);
        if (process == null) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message039", pid));
            return UniversalScriptCommand.COMMAND_ERROR;
        } else {
            this.process = process;
        }

        // 计算等待命令的超时时间，单位秒
        UniversalScriptCommand command = Ensure.notNull(process.getCommand());
        String script = command.getScript();
        long timeout = new MillisExpression(timeoutExpression).value();
        boolean usetimeout = timeout > 0;
        if (usetimeout && print) {
            stdout.println(ResourcesUtils.getMessage("script.stdout.message036", script, Dates.format(timeout, TimeUnit.MILLISECONDS, true)));
        } else {
            stdout.println(ResourcesUtils.getMessage("script.stdout.message037", script));
        }

        boolean stop = false;
        while (process.isAlive()) {
            if (this.terminate || session.isTerminate() || (usetimeout && ((System.currentTimeMillis() - compileMillis) > timeout) && !stop)) {
                try {
                    process.terminate();
                    continue;
                } catch (Throwable e) {
                    stderr.println(ResourcesUtils.getMessage("script.stderr.message024"), e);
                } finally {
                    stop = true;
                    stderr.println(ResourcesUtils.getMessage("script.stderr.message025", script));
                }
            }

            // 等待后台线程运行完毕
            long wait = timeout - (System.currentTimeMillis() - compileMillis);
            if (wait > 0) {
                process.getEnvironment().getWaitDone().sleep(wait);
            }

            // 命令被终止 或 用户会话被终止时，直接退出
            if (this.terminate || session.isTerminate()) {
                break;
            }
        }

        if (this.terminate) {
            return UniversalScriptCommand.TERMINATE;
        } else if (process.getExitcode() == null) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message007", this.command, script));
            return UniversalScriptCommand.COMMAND_ERROR;
        } else {
            Integer exitcode = process.getExitcode();
            if (print) {
                stdout.println(ResourcesUtils.getMessage("script.stdout.message038", script, exitcode));
            }
            return exitcode;
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.process != null) {
            this.process.terminate();
            this.process.getEnvironment().getWaitDone().wakeup();
        }
    }
}
