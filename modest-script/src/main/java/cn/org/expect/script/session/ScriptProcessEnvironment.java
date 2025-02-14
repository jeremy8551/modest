package cn.org.expect.script.session;

import java.io.File;

import cn.org.expect.concurrent.EasyCombine;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

public class ScriptProcessEnvironment {

    /** 命令信息 */
    protected volatile UniversalScriptCommand command;

    /** 脚本引擎上下文信息 */
    protected UniversalScriptContext context;

    /** 用户会话信息（执行后台命令的会话信息，可能与当前会话信息不同） */
    protected UniversalScriptSession session;

    /** 标准信息输出接口 */
    protected UniversalScriptStdout stdout;

    /** 错误信息输出接口 */
    protected UniversalScriptStderr stderr;

    /** 日志文件 */
    protected File logfile;

    /** true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值） */
    protected boolean forceStdout;

    /** 等待后台线程启动 */
    protected EasyCombine waitRun;

    /** 等待后台线程运行结束 */
    protected EasyCombine waitDone;

    public ScriptProcessEnvironment(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, File logfile) {
        this.waitRun = new EasyCombine();
        this.waitDone = new EasyCombine();
        this.session = session;
        this.context = context;
        this.stdout = stdout;
        this.stderr = stderr;
        this.forceStdout = forceStdout;
        this.command = command;
        this.logfile = logfile;
    }

    public UniversalScriptCommand getCommand() {
        return command;
    }

    public UniversalScriptContext getContext() {
        return context;
    }

    public UniversalScriptSession getSession() {
        return session;
    }

    public UniversalScriptStdout getStdout() {
        return stdout;
    }

    public UniversalScriptStderr getStderr() {
        return stderr;
    }

    public File getLogfile() {
        return logfile;
    }

    public boolean forceStdout() {
        return forceStdout;
    }

    public EasyCombine getWaitRun() {
        return waitRun;
    }

    public EasyCombine getWaitDone() {
        return waitDone;
    }
}
