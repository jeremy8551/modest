package cn.org.expect.script.session;

import java.util.Date;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Terminate;
import cn.org.expect.util.UniqueSequenceGenerator;

/**
 * 子线程信息
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptProcess implements Terminate {

    /** 任务编号的序号生成器 */
    protected final static UniqueSequenceGenerator UNIQUE = new UniqueSequenceGenerator("{}", 21);

    /** 任务编号 */
    private final String pid;

    /** 任务起始时间 */
    private Date startTime;

    /** 任务终止时间 */
    private Date endTime;

    /** 任务运行环境 */
    private final ScriptProcessEnvironment environment;

    /** 任务运行线程 */
    private final ScriptProcessJob scriptJob;

    /** 任务的返回值 */
    private Integer exitcode;

    /** 行号 */
    private final long lineNumber;

    /**
     * 初始化
     *
     * @param environment 运行环境
     * @param scriptJob   脚本任务
     */
    public ScriptProcess(ScriptProcessEnvironment environment, ScriptProcessJob scriptJob) {
        this.pid = UNIQUE.nextString();
        this.environment = Ensure.notNull(environment);
        this.scriptJob = Ensure.notNull(scriptJob);
        this.scriptJob.setObserver(this);
        this.lineNumber = this.environment.getSession().getCompiler().getLineNumber();
    }

    /**
     * 启动任务
     */
    public void start() {
        this.startTime = new Date();
        this.environment.getContext().getContainer().getBean(ThreadSource.class).getExecutorService().submit(this.scriptJob);
    }

    /**
     * 通知任务停止运行并保存返回值
     *
     * @param exitcode 退出值
     */
    public void notifyStop(Integer exitcode) {
        this.exitcode = exitcode;
        if (exitcode != null) {
            this.endTime = new Date();
        }
    }

    /**
     * 终止任务
     *
     * @throws Exception 发生错误
     */
    public void terminate() throws Exception {
        this.scriptJob.terminate();
    }

    /**
     * 判断任务是否已被终止
     *
     * @return 返回 true 表示任务已被终止
     */
    public boolean isTerminate() {
        return this.scriptJob.isTerminate();
    }

    /**
     * 判断任务是否正在运行
     *
     * @return 返回 true 表示任务正在运行
     */
    public boolean isAlive() {
        return this.scriptJob.isAlive() || this.scriptJob.isRunning();
    }

    /**
     * 判断任务是否还未开始执行
     *
     * @return 返回 true 表示任务还未开始执行, 返回 false 表示任务已终止或已开始执行
     */
    public boolean waitFor() {
        return !this.scriptJob.alreadyRun();
    }

    /**
     * 任务编号
     *
     * @return 任务编号
     */
    public String getPid() {
        return pid;
    }

    /**
     * 返回后台线程运行环境
     *
     * @return 运行环境信息
     */
    public ScriptProcessEnvironment getEnvironment() {
        return environment;
    }

    /**
     * 任务执行的命令
     *
     * @return 正在运行的命令
     */
    public UniversalScriptCommand getCommand() {
        return this.environment.getCommand();
    }

    /**
     * 任务的返回值
     *
     * @return 任务的返回值
     */
    public Integer getExitcode() {
        return exitcode;
    }

    /**
     * 任务起始时间
     *
     * @return 任务起始时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 任务结束时间
     *
     * @return 任务结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 返回行号
     *
     * @return 行号
     */
    public long getLineNumber() {
        return this.lineNumber;
    }
}
