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
        if (!StringUtils.isInt(this.number)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message093", this.command, this.number));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        Printer out;
        if ("out".equalsIgnoreCase(this.type)) {
            out = stdout;
        } else if ("err".equalsIgnoreCase(this.type)) {
            out = stderr;
        } else if ("step".equalsIgnoreCase(this.type)) {
            out = context.getEngine().getSteper();
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message094", this.command, this.type, "out, err, step"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        int total = Integer.parseInt(this.number);
        ScriptProgress progress;
        if (session.getAnalysis().isBlank(this.name)) {
            progress = new ScriptProgress(out, this.message, total);
        } else {
            progress = new ScriptProgress(this.name, out, this.message, total);
        }

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(progress.toString(this.isGlobal()));
        }

        ProgressMap.get(context, this.isGlobal()).add(progress);
        return 0;
    }
}
