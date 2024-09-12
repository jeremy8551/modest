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
    private String[] sessionid;

    /** 后台进程编号 */
    private String[] processid;

    public TerminateCommand(UniversalCommandCompiler compiler, String command, String[] sessionid, String[] processid) {
        super(compiler, command);
        this.sessionid = sessionid;
        this.processid = processid;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        // 终止后台进程
        boolean print = session.isEchoEnable() || forceStdout;
        UniversalScriptAnalysis analysis = session.getAnalysis();
        if (this.processid != null && this.processid.length > 0) {
            for (String str : this.processid) {
                String pid = analysis.replaceShellVariable(session, context, str, true, true, false, false);
                ScriptProcess process = session.getSubProcess().get(pid);
                if (process == null) {
                    return UniversalScriptCommand.COMMAND_ERROR;
                } else {
                    if (print) {
                        stdout.println(ResourcesUtils.getMessage("script.message.stdout009", "[" + pid + "] " + process.getCommand().getScript()));
                    }
                    process.terminate();
                }
            }
        }

        // 终止指定用户会话
        UniversalScriptSessionFactory manager = session.getSessionFactory();
        if (this.sessionid != null && this.sessionid.length > 0) {
            for (String str : this.sessionid) {
                String id = analysis.replaceShellVariable(session, context, str, true, true, false, false);
                UniversalScriptSession obj = manager.get(id);
                if (obj == null) {
                    return UniversalScriptCommand.COMMAND_ERROR;
                } else {
                    if (print) {
                        stdout.println(ResourcesUtils.getMessage("script.message.stdout008", id));
                    }
                    manager.terminate(id);
                }
            }
        }

        // 未设置用户会话id和后台进程id时，终止所有用户会话
        if (ArrayUtils.isEmpty(this.processid) && ArrayUtils.isEmpty(this.sessionid)) {
            if (print) {
                stdout.println(ResourcesUtils.getMessage("script.message.stdout004"));
            }
            manager.terminate();
            return UniversalScriptCommand.TERMINATE;
        }

        return session.isTerminate() ? UniversalScriptCommand.TERMINATE : 0;
    }

    public void terminate() throws Exception {
    }

}
