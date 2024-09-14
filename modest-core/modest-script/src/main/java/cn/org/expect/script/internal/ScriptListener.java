package cn.org.expect.script.internal;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

public class ScriptListener implements UniversalScriptListener {

    /** 家庭器类信息与监听器映射集合 */
    private LinkedHashMap<Class<?>, UniversalCommandListener> map;

    /** 监听器集合 */
    private ArrayList<UniversalCommandListener> list;

    public ScriptListener() {
        this.map = new LinkedHashMap<Class<?>, UniversalCommandListener>();
        this.list = new ArrayList<UniversalCommandListener>();
        this.add(new DefaultListener()); // 设置默认监听器
    }

    public List<UniversalCommandListener> values() {
        return this.list;
    }

    public boolean contains(Class<? extends UniversalCommandListener> cls) {
        return this.map.containsKey(cls);
    }

    public void add(UniversalCommandListener listener) {
        Ensure.notNull(listener);
        this.map.put(listener.getClass(), listener);
        this.list.clear(); // 先清空再添加
        Set<Class<?>> set = this.map.keySet();
        for (Class<?> cls : set) {
            UniversalCommandListener next = this.map.get(cls);
            this.list.add(next);
        }
    }

    public void addAll(UniversalScriptListener listener) {
        if (listener != null) {
            for (Iterator<UniversalCommandListener> it = listener.values().iterator(); it.hasNext(); ) {
                UniversalCommandListener obj = it.next();
                if (!(obj instanceof DefaultListener)) { // 不能重复添加
                    this.add(obj);
                }
            }
        }
    }

    public boolean remove(Class<? extends UniversalCommandListener> cls) {
        UniversalCommandListener listener = this.map.remove(cls);
        return listener != null && this.list.remove(listener);
    }

    public UniversalCommandListener get(Class<? extends UniversalCommandListener> cls) {
        return this.map.get(cls);
    }

    public void startScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalCommandListener listener = this.list.get(i);
            listener.startScript(session, context, stdout, stderr, forceStdout, in);
        }
    }

    public boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) throws Exception {
        boolean value = true;
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalCommandListener listener = this.list.get(i);
            if (!listener.beforeCommand(session, context, stdout, stderr, command)) {
                value = false;
            }
        }
        return value;
    }

    public void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception {
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalCommandListener listener = this.list.get(i);
            listener.afterCommand(session, context, stdout, stderr, forceStdout, command, result);
        }
    }

    public void catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Exception e) throws Exception {
        String script = (command == null) ? "" : command.getScript();
        if (this.list.isEmpty()) {
            throw new UniversalScriptException(script, e);
        } else {
            boolean value = false;
            for (int i = 0, size = this.list.size(); i < size; i++) {
                UniversalCommandListener listener = this.list.get(i);
                if (listener.catchCommand(session, context, stdout, stderr, forceStdout, command, result, e)) {
                    value = true;
                }
            }

            if (!value) { // 没有执行任何业务逻辑时，抛出异常信息
                throw e;
            }
        }
    }

    public void catchScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Exception e) throws Exception {
        String script = (command == null) ? "" : command.getScript();
        if (this.list.isEmpty()) {
            throw new UniversalScriptException(script, e);
        } else {
            boolean value = false;
            for (int i = 0, size = this.list.size(); i < size; i++) {
                UniversalCommandListener listener = this.list.get(i);
                if (listener.catchScript(session, context, stdout, stderr, forceStdout, command, result, e)) {
                    value = true;
                }
            }

            if (!value) { // 没有执行任何业务逻辑时，抛出异常信息
                throw e;
            }
        }
    }

    public void exitScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception {
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalCommandListener listener = this.list.get(i);
            listener.exitScript(session, context, stdout, stderr, forceStdout, command, result);
        }
    }

    /**
     * 脚本引擎默认监听器
     */
    static class DefaultListener implements UniversalCommandListener {

        public void startScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
            if (context.getEngine().isClose()) { // 脚本引擎已关闭
                throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr999", IO.read(in, new StringBuilder())));
            }
        }

        public boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) {
            return true;
        }

        public void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) {
        }

        public boolean catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) {
            return false;
        }

        public boolean catchScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Throwable e) {
            return false;
        }

        public void exitScript(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) {
            if (session.isTerminate()) { // 会话已被终止
                result.setExitcode(UniversalScriptCommand.TERMINATE);
                stderr.println(ResourcesUtils.getMessage("script.message.stderr046", session.getScriptName()));
            }

            // 打印发生错误的脚本行号及语句报错信息
            if (result.getExitcode() != 0 && session.isScriptFile()) {
                UniversalScriptCompiler compiler = session.getCompiler();
                stderr.println(ResourcesUtils.getMessage("script.message.stderr055", session.getScriptName(), compiler.getLineNumber(), command.getClass().getSimpleName(), StringUtils.escapeLineSeparator(command.getScript())));
            }
        }
    }

}
