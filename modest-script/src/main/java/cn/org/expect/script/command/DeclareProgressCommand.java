package cn.org.expect.script.command;

import cn.org.expect.printer.Printer;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ProgressMap;
import cn.org.expect.script.internal.ScriptProgress;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 建立进度输出: <br>
 * declare global {taskId} progress use {step} print {message} total {999} times; <br>
 * <br>
 * 输出进度信息: <br>
 * progress
 */
public class DeclareProgressCommand extends AbstractGlobalCommand {

    /** 任务编号（输出多任务信息时,用以区分不同任务） */
    private String name;

    /** 输出方式: out, err, step */
    private String type;

    /** 输出进度内容 */
    private String message;

    /** 总计次数 */
    private String number;

    public DeclareProgressCommand(UniversalCommandCompiler compiler, String command, String name, String type, String message, String number, boolean global) {
        super(compiler, command);
        this.name = name;
        this.type = type;
        this.message = message;
        this.number = number;
        this.setGlobal(global);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        String name = session.getAnalysis().replaceShellVariable(session, context, this.name, true, true);
        String type = session.getAnalysis().replaceShellVariable(session, context, this.type, true, true);
        String message = session.getAnalysis().replaceShellVariable(session, context, session.getAnalysis().unQuotation(this.message), true, !session.getAnalysis().containsQuotation(this.message));
        String number = session.getAnalysis().replaceShellVariable(session, context, this.number, true, true);

        if (!StringUtils.isInt(number)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message093", this.command, number));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        Printer out;
        if ("out".equalsIgnoreCase(type)) {
            out = stdout;
        } else if ("err".equalsIgnoreCase(type)) {
            out = stderr;
        } else if ("step".equalsIgnoreCase(type)) {
            out = context.getEngine().getSteper();
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message094", this.command, type, "out, err, step"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        int total = Integer.parseInt(number);
        ScriptProgress progress;
        if (session.getAnalysis().isBlank(name)) {
            progress = new ScriptProgress(out, message, total);
        } else {
            progress = new ScriptProgress(name, out, message, total);
        }

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(progress.toString(this.isGlobal()));
        }

        ProgressMap.get(context, this.isGlobal()).add(progress);
        return 0;
    }
}
