package cn.org.expect.script.internal;

import java.io.IOException;
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
import cn.org.expect.script.io.ScriptStderr;
import cn.org.expect.util.Ensure;

/**
 * 异常处理逻辑集合 <br>
 * declare (exit | continue) handler for ( exception | exitcode != 0 | sqlstate == '02501' | errorcode -803 ) begin .. end
 */
public class ProcessExceptionHandlerMap implements UniversalScriptProgram {

    private final static String ERROR_HANDLER_MAP = "ERROR_HANDLER_MAP";

    public static ProcessExceptionHandlerMap get(UniversalScriptContext context, boolean global) {
        ProcessExceptionHandlerMap obj = context.getProgram(ERROR_HANDLER_MAP, global);
        if (obj == null) {
            obj = new ProcessExceptionHandlerMap();
            context.addProgram(ERROR_HANDLER_MAP, obj, global);
        }
        return obj;
    }

    /** 无异常处理类 */
    public final static int EMPTY_HANDLER = -1;

    /** 退出 */
    public final static int EXIT_HANDLER = 0;

    /** 继续向下执行 */
    public final static int CONTINUE_HANDLER = 1;

    /** 执行条件与异常处理逻辑的映射关系 */
    private LinkedHashMap<String, ScriptHandler> map;

    /** true 表示 {@link ProcessExceptionHandlerMap#execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean, String, Throwable)}} 已经被执行了 */
    private boolean alreadyExecute;

    /**
     * 初始化
     */
    public ProcessExceptionHandlerMap() {
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
     * @param condition 异常错误处理逻辑的执行条件, 如: exception
     * @return 处理逻辑
     */
    public ScriptHandler remove(String condition) {
        return this.map.remove(ScriptHandler.toKey(Ensure.notBlank(condition)));
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
     * 返回所有异常处理逻辑
     *
     * @return 处理逻辑集合
     */
    public Collection<ScriptHandler> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    /**
     * 判断是否已执行过 {@linkplain #execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean, String, Throwable)} 方法
     *
     * @return 返回 true 表示该方法已被执行过
     */
    public boolean alreadyExecute() {
        return this.alreadyExecute;
    }

    /**
     * 执行java异常错误处理逻辑
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param message     命令语句
     * @param exception   java异常错误信息
     * @return -1表示没有异常处理逻辑，0表示退出脚本，1表示继续向下执行
     */
    public synchronized int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, String message, Throwable exception) {
        if (stderr == null) {
            stderr = new ScriptStderr();
        }

        if (this.map.isEmpty()) {
            return ProcessExceptionHandlerMap.EMPTY_HANDLER;
        }

        int exit = ProcessExceptionHandlerMap.EXIT_HANDLER;
        Set<String> names = this.map.keySet();
        for (String name : names) {
            ScriptHandler handler = this.map.get(name);
            if (handler != null && handler.executeException(session, context, stdout, stderr, forceStdout, message, exception)) {
                if (!handler.isReturnExit()) {
                    exit = ProcessExceptionHandlerMap.CONTINUE_HANDLER;
                }
            }
        }
        this.alreadyExecute = true;
        return exit;
    }

    public ScriptProgramClone deepClone() {
        ProcessExceptionHandlerMap obj = new ProcessExceptionHandlerMap();
        obj.alreadyExecute = this.alreadyExecute;
        Set<Entry<String, ScriptHandler>> set = this.map.entrySet();
        for (Entry<String, ScriptHandler> e : set) {
            String key = e.getKey();
            ScriptHandler val = e.getValue().clone();
            obj.map.put(key, val);
        }
        return new ScriptProgramClone(ERROR_HANDLER_MAP, obj);
    }

    public void close() throws IOException {
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
