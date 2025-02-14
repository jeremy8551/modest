package cn.org.expect.script.internal;

import java.io.Reader;

import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptListener;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.command.ExitCommand;
import cn.org.expect.script.command.ReturnCommand;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.ResourcesUtils;

/**
 * 监听器实现类
 */
public class CommandHandlerListener implements UniversalScriptListener {

    public CommandHandlerListener() {
    }

    public void startEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
    }

    public boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) throws Exception {
        return true;
    }

    public void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception {
        int exitcode = result.getExitcode();
        if (!ClassUtils.inArray(command.getClass(), ReturnCommand.class, ExitCommand.class) && exitcode != 0) {
            boolean gv = ProcessExitcodeHandlerMap.get(context, true).execute(session, context, stdout, stderr, forceStdout, exitcode);
            boolean lv = ProcessExitcodeHandlerMap.get(context, false).execute(session, context, stdout, stderr, forceStdout, exitcode);
            result.setExitSession(gv && lv);
        }
    }

    public boolean catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception {
        result.setExitcode(UniversalScriptCommand.ERROR);

        // 保存最后一个异常错误信息
        UniversalScriptCompiler compiler = session.getCompiler();
        session.addVariable(UniversalScriptVariable.SESSION_VARNAME_LASTEXCEPTION, new UniversalScriptException(command.getScript(), compiler.getLineNumber(), e));

        ProcessExceptionHandlerMap gm = ProcessExceptionHandlerMap.get(context, true); // 使用全局异常处理逻辑
        ProcessExceptionHandlerMap lm = ProcessExceptionHandlerMap.get(context, false); // 使用局部异常处理逻辑
        int gv = gm.execute(session, context, stdout, stderr, forceStdout, command.getScript(), e);
        int lv = lm.execute(session, context, stdout, stderr, forceStdout, command.getScript(), e);
        if (gv == ProcessExceptionHandlerMap.CONTINUE_HANDLER || lv == ProcessExceptionHandlerMap.CONTINUE_HANDLER) {
            result.setExitSession(false);
            stdout.println(ResourcesUtils.getMessage("script.stdout.message015", command.getScript()));
        } else if (gv == ProcessExceptionHandlerMap.EMPTY_HANDLER && lv == ProcessExceptionHandlerMap.EMPTY_HANDLER) {
            result.setExitSession(true);
            stderr.println(ResourcesUtils.getMessage("script.stderr.message008", command.getScript()), e);
        } else if (gv == ProcessExceptionHandlerMap.EXIT_HANDLER || lv == ProcessExceptionHandlerMap.EXIT_HANDLER) {
            result.setExitSession(true);
            stdout.println(ResourcesUtils.getMessage("script.stdout.message014", command.getScript()), e);
        } else {
            result.setExitSession(true);
            stderr.println(ResourcesUtils.getMessage("script.stderr.message051", session.getScriptName(), gv));
        }
        return true;
    }

    public boolean catchEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception {
        UniversalScriptCompiler compiler = session.getCompiler();
        String message = ResourcesUtils.getMessage("script.stderr.message050", session.getScriptName(), compiler.getLineNumber());
        ProcessExceptionHandlerMap gl = ProcessExceptionHandlerMap.get(context, true);
        ProcessExceptionHandlerMap ll = ProcessExceptionHandlerMap.get(context, false);
        if (gl.alreadyExecute() || ll.alreadyExecute()) {
            stderr.println(message, e);
        } else {
            int gv = gl.execute(session, context, stdout, stderr, forceStdout, message, e);
            int lv = ll.execute(session, context, stdout, stderr, forceStdout, message, e);
            if (gv == ProcessExceptionHandlerMap.CONTINUE_HANDLER || lv == ProcessExceptionHandlerMap.CONTINUE_HANDLER) {
                stdout.println(ResourcesUtils.getMessage("script.stdout.message017", message));
            } else if (gv == ProcessExceptionHandlerMap.EMPTY_HANDLER && lv == ProcessExceptionHandlerMap.EMPTY_HANDLER) {
                stderr.println(message, e);
            } else {
                stdout.println(ResourcesUtils.getMessage("script.stdout.message016", message), e);
            }
        }
        return true;
    }

    public void exitEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) {
    }
}
