package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptListener;
import cn.org.expect.script.UniversalScriptListenerList;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.session.ScriptStep;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 使脚本引擎跳转到指定 step 命令后再向下执行命令
 */
public class JumpCommand extends AbstractTraceCommand implements UniversalScriptInputStream, JumpCommandSupported, LoopCommandSupported {

    /** 跳转的目的地 */
    private String message;

    public JumpCommand(UniversalCommandCompiler compiler, String command, String message) {
        super(compiler, command);
        this.message = message;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.message)) {
            this.message = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "jump", this.message);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String target = analysis.replaceShellVariable(session, context, this.message, true, false);
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(ResourcesUtils.getMessage("script.stdout.message033", target));
        }

        // 保存目标位置信息
        ScriptStep.get(context, true).setTarget(target);
        context.addGlobalVariable(UniversalScriptVariable.SESSION_VARNAME_JUMP, "true"); // JUMP 命令标识变量

        // 添加 jump 命令的监听器
        UniversalScriptListenerList list = context.getListenerList();
        if (!list.contains(JumpListener.class)) {
            list.add(new JumpListener());
        }
        return 0;
    }

    public boolean enableJump() {
        return true;
    }

    public boolean enableLoop() {
        return false;
    }

    public static class JumpListener implements UniversalScriptListener {

        public JumpListener() {
            super();
        }

        public void startEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
        }

        public boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) throws Exception {
            boolean value = ScriptStep.get(context, true).containsTarget() && (command instanceof JumpCommandSupported) && ((JumpCommandSupported) command).enableJump();
            return !value;
        }

        public void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception {
        }

        public boolean catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception {
            return false;
        }

        public boolean catchEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) throws Exception {
            return false;
        }

        public void exitEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) {
        }
    }
}
