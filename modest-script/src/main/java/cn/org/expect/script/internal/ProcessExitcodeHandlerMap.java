package cn.org.expect.script.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptProgram;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.Ensure;

/**
 * 异常处理逻辑集合 <br>
 * declare (exit | continue) handler for (  exitcode != 0 ) begin .. end
 */
public class ProcessExitcodeHandlerMap implements UniversalScriptProgram {

    private final static String EXIT_HANDLER_MAP = "EXIT_HANDLER_MAP";

    public static ProcessExitcodeHandlerMap get(UniversalScriptContext context, boolean global) {
        ProcessExitcodeHandlerMap obj = context.getProgram(EXIT_HANDLER_MAP, global);
        if (obj == null) {
            obj = new ProcessExitcodeHandlerMap();
            context.addProgram(EXIT_HANDLER_MAP, obj, global);
        }
        return obj;
    }

    /** 执行条件与异常错误处理逻辑映射关系 */
    private LinkedHashMap<String, ScriptHandler> map;

    /** true 表示 {@link ProcessExitcodeHandlerMap#execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean, Integer)} 方法已被执行过 */
    private boolean alreadyExecute;

    /**
     * 初始化
     */
    public ProcessExitcodeHandlerMap() {
        this.map = new LinkedHashMap<String, ScriptHandler>();
    }

    /**
     * 添加异常错误处理逻辑
     *
     * @param handler 异常错误处理逻辑
     * @return 处理逻辑
     */
    public ScriptHandler add(ScriptHandler handler) {
        Ensure.notNull(handler);
        return this.map.put(handler.getKey(), handler);
    }

    /**
     * 删除异常错误处理逻辑
     *
     * @param condition 异常处理逻辑执行条件，如：exitcode != 0
     * @return 处理逻辑
     */
    public ScriptHandler remove(String condition) {
        return this.map.remove(ScriptHandler.toKey(Ensure.notBlank(condition)));
    }

    /**
     * 返回所有异常处理逻辑
     *
     * @return 处理逻辑集合
     */
    public Collection<ScriptHandler> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    /**
     * 返回异常处理逻辑个数
     *
     * @return 处理逻辑个数
     */
    public int size() {
        return this.map.size();
    }

    /**
     * 判断是否执行过 {@link #execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean, Integer)} 方法
     *
     * @return 返回true表示 {@link #execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean, Integer)} 方法已被执行过
     */
    public boolean alreadyExecute() {
        return this.alreadyExecute;
    }

    /**
     * 执行异常错误处理逻辑
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param exitcode    脚本命令的返回值
     * @return true 表示退出执行, false 表示继续向下执行
     */
    public synchronized boolean execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Integer exitcode) {
        if (this.map.isEmpty()) {
            return true;
        }

        this.alreadyExecute = true;
        boolean exit = true;
        Set<String> keys = this.map.keySet();
        for (String key : keys) {
            ScriptHandler handler = this.map.get(key);
            if (handler != null && handler.executeExitcode(session, context, stdout, stderr, forceStdout, exitcode)) {
                exit = handler.isReturnExit();
            }
        }
        return exit;
    }

    public ScriptProgramClone deepClone() {
        ProcessExitcodeHandlerMap obj = new ProcessExitcodeHandlerMap();
        obj.alreadyExecute = this.alreadyExecute;
        Set<Entry<String, ScriptHandler>> set = this.map.entrySet();
        for (Entry<String, ScriptHandler> e : set) {
            String key = e.getKey();
            ScriptHandler val = e.getValue().clone();
            obj.map.put(key, val);
        }
        return new ScriptProgramClone(EXIT_HANDLER_MAP, obj);
    }

    public void close() {
        Collection<ScriptHandler> values = this.map.values();
        for (ScriptHandler handler : values) {
            if (handler != null) {
                handler.clear();
            }
        }

        this.map.clear();
        this.alreadyExecute = false;
    }
}
