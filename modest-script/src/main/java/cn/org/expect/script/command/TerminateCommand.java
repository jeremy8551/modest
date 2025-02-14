package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptSessionFactory;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.session.ScriptProcess;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ResourcesUtils;

/**
 * 终止当前脚本命令
 */
public class TerminateCommand extends AbstractTraceCommand {

    /** 用户会话编号 */
    private final String[] sessionIds;

    /** 后台进程编号 */
    private final String[] processIds;

    public TerminateCommand(UniversalCommandCompiler compiler, String command, String[] sessionIds, String[] processIds) {
        super(compiler, command);
        this.sessionIds = sessionIds;
        this.processIds = processIds;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        // 终止后台线程
        boolean print = session.isEchoEnable() || forceStdout;
        UniversalScriptAnalysis analysis = session.getAnalysis();
        if (this.processIds != null) {
            for (String str : this.processIds) {
                String pid = analysis.replaceShellVariable(session, context, str, true, false);
                ScriptProcess process = session.getSubProcess().get(pid);
                if (process == null) {
                    return UniversalScriptCommand.COMMAND_ERROR;
                } else {
                    if (print) {
                        stdout.println(ResourcesUtils.getMessage("script.stdout.message013", pid, process.getCommand().getScript()));
                    }
                    process.terminate();
                }
            }
        }

        // 终止指定用户会话
        UniversalScriptSessionFactory manager = session.getSessionFactory();
        if (this.sessionIds != null) {
            for (String str : this.sessionIds) {
                String id = analysis.replaceShellVariable(session, context, str, true, false);
                UniversalScriptSession obj = manager.get(id);
                if (obj == null) {
                    return UniversalScriptCommand.COMMAND_ERROR;
                } else {
                    if (print) {
                        stdout.println(ResourcesUtils.getMessage("script.stdout.message012", id));
                    }
                    manager.terminate(id);
                }
            }
        }

        // 终止所有用户会话（未设置用户会话id和后台进程id时）
        if (ArrayUtils.isEmpty(this.processIds) && ArrayUtils.isEmpty(this.sessionIds)) {
            if (print) {
                stdout.println(ResourcesUtils.getMessage("script.stdout.message008"));
            }
            manager.terminate();
            return UniversalScriptCommand.TERMINATE;
        }

        return session.isTerminate() ? UniversalScriptCommand.TERMINATE : 0;
    }
}
