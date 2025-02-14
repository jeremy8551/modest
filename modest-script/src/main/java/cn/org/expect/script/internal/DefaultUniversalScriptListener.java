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
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;

/**
 * 默认监听器
 */
public class DefaultUniversalScriptListener implements UniversalScriptListener {

    public DefaultUniversalScriptListener() {
    }

    public boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) {
        return true;
    }

    public void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) {
    }

    public boolean catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) {
        return false;
    }

    public void startEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
        if (context.getEngine().isClose()) { // 脚本引擎已关闭
            throw new UniversalScriptException("script.stderr.message114", IO.read(in, new StringBuilder()));
        }
    }

    public void exitEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) {
        if (session.isTerminate()) { // 会话已被终止
            result.setExitcode(cn.org.expect.script.UniversalScriptCommand.TERMINATE);
            stderr.println(ResourcesUtils.getMessage("script.stderr.message041", session.getScriptName()));
        }

        // 打印发生错误的脚本行号及语句报错信息
        if (result.getExitcode() != 0 && session.isScriptFile()) {
            UniversalScriptCompiler compiler = session.getCompiler();
            stderr.println(ResourcesUtils.getMessage("script.stderr.message049", command.getScript(), session.getScriptName(), compiler.getLineNumber(), command.getClass().getName()));
        }
    }

    public boolean catchEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) {
        return false;
    }
}
