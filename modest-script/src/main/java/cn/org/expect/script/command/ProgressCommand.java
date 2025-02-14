package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ProgressMap;
import cn.org.expect.script.internal.ScriptProgress;
import cn.org.expect.util.ResourcesUtils;

/**
 * 打印进度信息
 */
public class ProgressCommand extends AbstractTraceCommand {

    /** 任务编号 */
    private final String taskId;

    public ProgressCommand(UniversalCommandCompiler compiler, String command, String taskId) {
        super(compiler, command);
        this.taskId = taskId;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        ScriptProgress progress;
        if (this.taskId.length() == 0) { // 使用默认的进度输出组件
            progress = ProgressMap.getProgress(context);
        } else { // 查询多任务进度输出组件
            UniversalScriptAnalysis analysis = session.getAnalysis();
            String name = analysis.replaceShellVariable(session, context, this.taskId, true, true);
            progress = ProgressMap.getProgress(context, name);
        }

        if (progress == null) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message061", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        } else {
            progress.print(session.isEchoEnable() || forceStdout);
            return 0;
        }
    }
}
