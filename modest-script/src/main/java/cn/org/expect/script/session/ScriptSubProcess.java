package cn.org.expect.script.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.util.Ensure;

/**
 * 子线程
 */
public class ScriptSubProcess {
    private final static Log log = LogFactory.getLog(ScriptSubProcess.class);

    /** 命令编号与命令的映射关系 */
    private LinkedHashMap<String, ScriptProcess> map;

    /**
     * 初始化
     */
    public ScriptSubProcess() {
        this.map = new LinkedHashMap<String, ScriptProcess>();
    }

    /**
     * 创建子线程
     *
     * @param environment 运行环境
     * @return 脚本引擎进程
     */
    public ScriptProcess create(ScriptProcessEnvironment environment) {
        ScriptProcessJob scriptJob = new ScriptProcessJob(environment);
        ScriptProcess process = new ScriptProcess(environment, scriptJob);
        this.map.put(process.getPid(), process);
        return process;
    }

    /**
     * 判断是否已添加命令
     *
     * @param pid 进程编号
     * @return 返回true表示已添加命令
     */
    public boolean contains(String pid) {
        return this.map.containsKey(pid);
    }

    /**
     * 返回进程
     *
     * @param pid 进程编号
     * @return 进程
     */
    public ScriptProcess get(String pid) {
        return this.map.get(Ensure.notBlank(pid));
    }

    /**
     * 移除进程
     *
     * @param pid 进程编号
     */
    public ScriptProcess remove(String pid) {
        return this.map.remove(pid);
    }

    /**
     * 返回子线程
     *
     * @return 子线程集合
     */
    public List<ScriptProcess> getThreads() {
        return new ArrayList<ScriptProcess>(this.map.values());
    }

    /**
     * 终止所有后台进程
     */
    public void terminate() {
        Throwable exception = null;
        for (Iterator<ScriptProcess> it = this.map.values().iterator(); it.hasNext(); ) {
            ScriptProcess process = it.next();
            if (process != null) {
                try {
                    process.terminate();
                } catch (Throwable e) {
                    if (log.isErrorEnabled()) {
                        log.error("script.stdout.message013", process.getPid(), process.getCommand() != null ? process.getCommand().getScript() : "", e);
                    }
                    exception = e;
                }
            }
        }

        if (exception != null) {
            throw new UniversalScriptException("script.stderr.message024", exception);
        }
    }

    /**
     * 移除所有进程
     */
    public void clear() {
        this.map.clear();
    }
}
