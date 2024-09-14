package cn.org.expect.script.command;

import java.io.Reader;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandListener;
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
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.internal.ErrorHandlerMap;
import cn.org.expect.script.internal.ExitHandlerMap;
import cn.org.expect.script.internal.ScriptHandler;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 建立异常处理逻辑
 * <p>
 * declare [global] (exit | continue) handler for ( exception | exitcode != 0 | sqlstate == '02501' | errorcode -803 ) begin .. end 语句
 */
public class DeclareHandlerCommand extends AbstractGlobalCommand implements LoopCommandSupported, WithBodyCommandSupported {

    /** 异常处理逻辑的执行方法 */
    private CommandList body;

    /** exit 或 continue */
    private String exitOrContinue;

    /** 异常处理逻辑的执行条件：exception | exitcode != 0 | sqlstate == '02501' | errorcode -803 */
    private String condition;

    /** true表示退出处理逻辑 false表示错误处理逻辑 */
    private boolean isExitHandler;

    public DeclareHandlerCommand(UniversalCommandCompiler compiler, String command, CommandList body, String exitOrContinue, String condition, boolean isExitHandler, boolean global) {
        super(compiler, command);
        this.body = body;
        this.body.setOwner(this);
        this.exitOrContinue = exitOrContinue;
        this.condition = condition;
        this.isExitHandler = isExitHandler;
        this.setGlobal(global);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(StringUtils.escapeLineSeparator(this.command));
        }

        boolean global = this.isGlobal();
        ScriptHandler handler = new ScriptHandler(this.exitOrContinue, this.condition, this.body);
        if (this.isExitHandler) {
            ExitHandlerMap.get(context, global).add(handler);
        } else {
            ErrorHandlerMap.get(context, global).add(handler);
        }

        UniversalScriptListener c = context.getCommandListeners();
        if (!c.contains(HandlerListener.class)) {
            c.add(new HandlerListener());
        }
        return 0;
    }

    /**
     * 监听器实现类
     */
    class HandlerListener implements UniversalCommandListener {

        public HandlerListener() {
        }

        public void startScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
        }

        public boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) throws Exception {
            return true;
        }

        public void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception {
            int exitcode = result.getExitcode();
            if (!ClassUtils.inArray(command.getClass(), ReturnCommand.class, ExitCommand.class) && exitcode != 0) {
                boolean gv = ExitHandlerMap.get(context, true).execute(session, context, stdout, stderr, forceStdout, exitcode);
                boolean lv = ExitHandlerMap.get(context, false).execute(session, context, stdout, stderr, forceStdout, exitcode);
                if (gv && lv) {
                    result.setExitSession(true);
                } else {
                    result.setExitSession(false);
                }
            }
        }

        public boolean catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception {
            result.setExitcode(UniversalScriptCommand.ERROR);

            // 保存最后一个异常错误信息
            UniversalScriptCompiler compiler = session.getCompiler();
            session.addVariable(UniversalScriptVariable.SESSION_VARNAME_LASTEXCEPTION, new UniversalScriptException(e, compiler.getLineNumber(), command.getScript()));

            ErrorHandlerMap gm = ErrorHandlerMap.get(context, true); // 使用全局异常处理逻辑
            ErrorHandlerMap lm = ErrorHandlerMap.get(context, false); // 使用局部异常处理逻辑
            int gv = gm.catchCommandError(session, context, stdout, stderr, forceStdout, command.getScript(), e);
            int lv = lm.catchCommandError(session, context, stdout, stderr, forceStdout, command.getScript(), e);
            if (gv == ErrorHandlerMap.CONTINUE_HANDLER || lv == ErrorHandlerMap.CONTINUE_HANDLER) {
                result.setExitSession(false);
                stdout.println(ResourcesUtils.getMessage("script.message.stdout011", command.getScript()));
            } else if (gv == ErrorHandlerMap.EMPTY_HANDLER && lv == ErrorHandlerMap.EMPTY_HANDLER) {
                result.setExitSession(true);
                stderr.println(ResourcesUtils.getMessage("script.message.stderr010", command.getScript()), e);
            } else if (gv == ErrorHandlerMap.EXIT_HANDLER || lv == ErrorHandlerMap.EXIT_HANDLER) {
                result.setExitSession(true);
                stdout.println(ResourcesUtils.getMessage("script.message.stdout010", command.getScript()), e);
            } else {
                result.setExitSession(true);
                stderr.println(ResourcesUtils.getMessage("script.message.stderr057", session.getScriptName(), gv));
            }
            return true;
        }

        public boolean catchScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception {
            UniversalScriptCompiler compiler = session.getCompiler();
            String message = ResourcesUtils.getMessage("script.message.stderr056", session.getScriptName(), compiler.getLineNumber());
            ErrorHandlerMap gl = ErrorHandlerMap.get(context, true);
            ErrorHandlerMap ll = ErrorHandlerMap.get(context, false);
            if (gl.alreadyCatchEvalError() || ll.alreadyCatchEvalError()) {
                stderr.println(message, e);
            } else {
                int gv = gl.catchEvalError(session, context, stdout, stderr, forceStdout, message, e);
                int lv = ll.catchEvalError(session, context, stdout, stderr, forceStdout, message, e);
                if (gv == ErrorHandlerMap.CONTINUE_HANDLER || lv == ErrorHandlerMap.CONTINUE_HANDLER) {
                    stdout.println(ResourcesUtils.getMessage("script.message.stdout013", message));
                } else if (gv == ErrorHandlerMap.EMPTY_HANDLER && lv == ErrorHandlerMap.EMPTY_HANDLER) {
                    stderr.println(message, e);
                } else {
                    stdout.println(ResourcesUtils.getMessage("script.message.stdout012", message), e);
                }
            }
            return true;
        }

        public void exitScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) {
        }
    }

    public void terminate() throws Exception {
    }

    public boolean enableLoop() {
        return false;
    }

}
