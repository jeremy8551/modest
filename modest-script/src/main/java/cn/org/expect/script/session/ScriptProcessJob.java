package cn.org.expect.script.session;

import java.io.File;
import java.io.Writer;

import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.io.ScriptStderr;
import cn.org.expect.script.io.ScriptStdout;
import cn.org.expect.script.io.ScriptWriterFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Terminator;

/**
 * 脚本引擎上运行的并发任务
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptProcessJob extends Terminator implements Runnable {

    /** true表示线程正在执行或已执行过 {@linkplain ScriptProcessJob#run()} 方法 */
    private volatile boolean alreadyRun;

    /** true表示正在执行 {@linkplain ScriptProcessJob#run()} 方法 */
    private volatile boolean running;

    /** true表示任务存活（还未运行或正在运行）false表示任务已运行完毕 */
    private volatile boolean alive;

    /** 进程运行环境 */
    private final ScriptProcessEnvironment environment;

    /** 进程 */
    private ScriptProcess processInfo;

    /**
     * 初始化
     *
     * @param environment 进程的运行环境
     */
    public ScriptProcessJob(ScriptProcessEnvironment environment) {
        super();
        this.alreadyRun = false;
        this.running = false;
        this.terminate = false;
        this.environment = environment;
        this.alive = true;
    }

    /**
     * 设置子线程
     *
     * @param process 子线程
     */
    public void setObserver(ScriptProcess process) {
        this.processInfo = Ensure.notNull(process);
    }

    /**
     * 判断任务是否已终止
     *
     * @return 返回true表示任务存活（还未运行或正在运行）false表示任务已运行完毕
     */
    public boolean isAlive() {
        return this.alive;
    }

    /**
     * 运行线程
     */
    public void run() {
        this.alreadyRun = true;
        this.environment.getWaitRun().wakeup(); // 唤醒等待启动的线程

        Writer out = null;
        ScriptStdout cmdout = null;
        ScriptStderr cmderr = null;
        UniversalScriptSession session = this.environment.getSession();
        UniversalScriptContext context = this.environment.getContext();
        UniversalScriptStdout stdout = this.environment.getStdout();
        UniversalScriptStderr stderr = this.environment.getStderr();
        UniversalScriptCommand command = this.environment.getCommand();
        ScriptMainProcess process = session.getMainProcess();
        boolean forceStdout = this.environment.forceStdout();
        File logfile = this.environment.getLogfile();
        try {
            this.running = true;

            int exitcode;
            if (this.terminate) {
                exitcode = UniversalScriptCommand.TERMINATE;
            } else {
                FileUtils.assertCreateFile(logfile); // 创建日志文件

                // 标准信息与错误信息均写入日志文件
                ScriptWriterFactory factory = new ScriptWriterFactory(logfile.getAbsolutePath(), true);
                out = factory.build(session, context);
                cmdout = new ScriptStdout(out, stdout.getFormatter());
                cmderr = new ScriptStderr(out, stderr.getFormatter());

                UniversalCommandResultSet result = process.execute(session, context, cmdout, cmderr, forceStdout, command);
                exitcode = result.getExitcode();
            }

            // 通知进程停止
            this.processInfo.notifyStop(exitcode);
        } catch (Throwable e) {
            if (cmderr == null) {
                stderr.println(command.getScript(), e);
            } else {
                cmderr.println(command.getScript(), e);
            }
            throw new UniversalScriptException(command.getScript(), e);
        } finally {
            this.running = false;
            this.alive = false;
            this.environment.getWaitDone().wakeup();

            // 因为 cmdout 与 cmderr 公用一个 out，所以关闭流时需要先将缓存清空，再一个一个关闭流
            if (cmdout != null) {
                cmdout.flush();
                cmdout.setWriter(null);
            }

            if (cmderr != null) {
                cmderr.flush();
                cmderr.setWriter(null);
            }

            IO.close(out, cmdout, cmderr);
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.environment.getCommand() != null && this.running) {
            try {
                this.environment.getCommand().terminate();
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * 判断任务已运行过
     *
     * @return 返回true表示任务已运行 false表示任务还未执行
     */
    public boolean alreadyRun() {
        return this.alreadyRun;
    }

    /**
     * 判断任务是否正在运行
     *
     * @return 返回true表示正在运行任务
     */
    public boolean isRunning() {
        return this.running;
    }
}
