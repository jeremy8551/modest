package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptListenerList;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptSteper;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.command.feature.CallbackCommandSupported;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.session.ScriptStep;
import cn.org.expect.util.ResourcesUtils;

/**
 * 建立步骤信息
 */
public class StepCommand extends AbstractTraceCommand implements CallbackCommandSupported, LoopCommandSupported {

    /** 步骤名 */
    private String message;

    public StepCommand(UniversalCommandCompiler compiler, String command, String message) {
        super(compiler, command);
        this.message = message;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String message = analysis.unQuotation(analysis.replaceShellVariable(session, context, this.message, true, false));
        boolean print = session.isEchoEnable() || forceStdout;
        ScriptStep obj = ScriptStep.get(context, true);

        // step 命令中不能包含 || 符号
        if (message.indexOf('\'') != -1 || message.indexOf('|') != -1) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message040", message));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        // 打印上一个步骤信息
        else if (analysis.isBlank(message)) {
            if (print) {
                stdout.println(obj.getStep());
            }
            return 0;
        }

        // 判断 step 命令是否重复
        else if (obj.containsStep(message)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message023", this.command, message));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        // 表示正在执行 jump 命令
        else if (obj.containsTarget()) {
            session.addVariable(UniversalScriptVariable.SESSION_VARNAME_STEP, message);
            if (message.equals(obj.getTarget())) { // 找到了 jump 命令对应的 step 命令
                UniversalScriptListenerList set = context.getListenerList();
                set.remove(JumpCommand.JumpListener.class);
                obj.removeTarget();
                context.addGlobalVariable(UniversalScriptVariable.SESSION_VARNAME_JUMP, "false"); // 移除标示变量
                obj.addStep(message);
                if (print) {
                    stdout.println(ResourcesUtils.getMessage("script.stdout.message034", message));
                }
                return this.execute(session, context, stdout, stderr, forceStdout, context.getEngine().getSteper(), message);
            } else { // 未找到对应的 step 命令
                if (print) {
                    stdout.println(ResourcesUtils.getMessage("script.stdout.message035", message));
                }
                return 0;
            }
        }

        // 成功添加一个步骤信息
        else {
            session.addVariable(UniversalScriptVariable.SESSION_VARNAME_STEP, message);
            obj.addStep(message);
            return this.execute(session, context, stdout, stderr, forceStdout, context.getEngine().getSteper(), message);
        }
    }

    /**
     * 发送步骤信息
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param steper      步骤信息输出接口
     * @param message     步骤信息
     * @throws Exception 发生错误
     */
    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptSteper steper, String message) throws Exception {
        if (steper.getWriter() == null) {
            if (session.isEchoEnable() || forceStdout) {
                stdout.println("step " + message);
            }
        } else {
            steper.println("step " + message);
        }
        return 0;
    }

    public boolean enableLoop() {
        return false;
    }

    public String[] getArguments() {
        return new String[]{"step", this.message};
    }
}
